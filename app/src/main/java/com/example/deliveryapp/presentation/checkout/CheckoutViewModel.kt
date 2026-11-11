package com.example.deliveryapp.presentation.checkout

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.data.repository.OrderRepository
import com.example.deliveryapp.data.repository.ProfileRepository
import com.example.deliveryapp.domain.logic.DeliverySimulator
import com.example.deliveryapp.domain.model.CartItem
import com.example.deliveryapp.domain.model.Order
import com.example.deliveryapp.domain.model.OrderPricing
import com.example.deliveryapp.domain.model.PaymentMethod
import com.example.deliveryapp.domain.model.UserProfile
import com.example.deliveryapp.domain.repository.DeliveryRepository
import com.example.deliveryapp.domain.usecase.GetCartItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import javax.inject.Inject

enum class CheckoutStage { EDITING, PROCESSING, SUCCESS }

data class CheckoutUiState(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val payment: PaymentMethod = PaymentMethod.CARD,
    val saveToProfile: Boolean = true,
    val nameError: Boolean = false,
    val phoneError: Boolean = false,
    val addressError: Boolean = false,
    /** true si el usuario aún no tenía sus datos completos en el perfil. */
    val profileWasIncomplete: Boolean = false,
    val stage: CheckoutStage = CheckoutStage.EDITING,
    val placedOrderId: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    getCartItemsUseCase: GetCartItemsUseCase,
    private val deliveryRepository: DeliveryRepository,
    private val profileRepository: ProfileRepository,
    private val orderRepository: OrderRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val items: StateFlow<List<CartItem>> = getCartItemsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _state = MutableStateFlow(CheckoutUiState())
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    /** Se llama al entrar a la pantalla: precarga los datos guardados en el perfil. */
    fun start() {
        val profile = profileRepository.load()
        _state.value = CheckoutUiState(
            name = profile.name,
            phone = profile.phone,
            address = profile.address,
            profileWasIncomplete = !profile.isComplete
        )
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = false) }

    fun onPhoneChange(value: String) = _state.update {
        it.copy(phone = value.filter { c -> c.isDigit() || c == '+' || c == ' ' }, phoneError = false)
    }

    fun onAddressChange(value: String) = _state.update { it.copy(address = value, addressError = false) }

    fun onPaymentChange(method: PaymentMethod) = _state.update { it.copy(payment = method) }

    fun onSaveToProfileChange(value: Boolean) = _state.update { it.copy(saveToProfile = value) }

    fun placeOrder() {
        val s = _state.value
        if (s.stage != CheckoutStage.EDITING) return

        val nameError = s.name.trim().length < 3
        val phoneError = s.phone.count { it.isDigit() } < 10
        val addressError = s.address.trim().length < 8
        if (nameError || phoneError || addressError) {
            _state.update {
                it.copy(nameError = nameError, phoneError = phoneError, addressError = addressError)
            }
            return
        }

        val cart = items.value
        if (cart.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(stage = CheckoutStage.PROCESSING) }

            // Pago SIMULADO: solo una pausa para que parezca que se procesa
            delay(1_800)

            val (lat, lng) = resolveDestination(s.address)
            val now = System.currentTimeMillis()
            val subtotal = cart.sumOf { it.subtotal }
            val fee = OrderPricing.deliveryFee(subtotal)

            val order = Order(
                id = "RP-" + now.toString().takeLast(6),
                createdAt = now,
                itemsSummary = cart.joinToString(", ") { "${it.quantity}× ${it.dish.name}" },
                subtotal = subtotal,
                deliveryFee = fee,
                total = subtotal + fee,
                customerName = s.name.trim(),
                phone = s.phone.trim(),
                address = s.address.trim(),
                paymentMethod = s.payment,
                destLat = lat,
                destLng = lng
            )
            orderRepository.insert(order)

            if (s.saveToProfile) {
                profileRepository.save(UserProfile(s.name, s.phone, s.address))
            }
            deliveryRepository.clearCart()

            _state.update { it.copy(stage = CheckoutStage.SUCCESS, placedOrderId = order.id) }
        }
    }

    /**
     * Intenta ubicar la dirección en el mapa. Si no se puede (sin internet, dirección
     * desconocida o muy lejos de la sucursal), usa un punto de entrega de ejemplo.
     */
    private suspend fun resolveDestination(address: String): Pair<Double, Double> {
        val lookup = viewModelScope.async(Dispatchers.IO) {
            runCatching {
                if (!Geocoder.isPresent()) {
                    null
                } else {
                    @Suppress("DEPRECATION")
                    Geocoder(context, Locale("es", "MX")).getFromLocationName(address, 1)?.firstOrNull()
                }
            }.getOrNull()
        }
        val found: Address? = withTimeoutOrNull(4_000) { lookup.await() }

        if (found != null) {
            val km = DeliverySimulator.distanceKm(
                DeliverySimulator.RESTAURANT_LAT, DeliverySimulator.RESTAURANT_LNG,
                found.latitude, found.longitude
            )
            if (km <= 15.0) return found.latitude to found.longitude
        }
        return DeliverySimulator.FALLBACK_DEST_LAT to DeliverySimulator.FALLBACK_DEST_LNG
    }
}
