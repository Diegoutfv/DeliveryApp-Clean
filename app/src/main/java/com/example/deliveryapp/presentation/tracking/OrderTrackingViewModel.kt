package com.example.deliveryapp.presentation.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.data.repository.OrderRepository
import com.example.deliveryapp.domain.logic.DeliverySimulator
import com.example.deliveryapp.domain.logic.DeliverySnapshot
import com.example.deliveryapp.domain.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TrackingUiState(
    val order: Order,
    val snapshot: DeliverySnapshot
)

/** Sigue un pedido de la app: el repartidor es simulado según el tiempo transcurrido. */
@HiltViewModel
class OrderTrackingViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    private val orderId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TrackingUiState?> = orderId
        .filterNotNull()
        .flatMapLatest { id ->
            repository.observeOrder(id)
                .filterNotNull()
                .flatMapLatest { order ->
                    ticker().map {
                        TrackingUiState(order, DeliverySimulator.snapshot(order, System.currentTimeMillis()))
                    }
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun load(id: String) {
        orderId.value = id
    }

    private fun ticker() = flow {
        while (true) {
            emit(Unit)
            delay(1_000)
        }
    }
}
