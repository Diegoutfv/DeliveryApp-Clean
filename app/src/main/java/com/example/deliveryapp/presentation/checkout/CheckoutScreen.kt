package com.example.deliveryapp.presentation.checkout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deliveryapp.domain.model.OrderPricing
import com.example.deliveryapp.domain.model.PaymentMethod
import com.example.deliveryapp.ui.components.PrimaryButton
import com.example.deliveryapp.ui.components.RossoTextField
import com.example.deliveryapp.ui.components.ScreenHeader
import com.example.deliveryapp.ui.components.formatMxn
import com.example.deliveryapp.ui.theme.Rosso
import com.example.deliveryapp.ui.theme.RossoDark
import kotlinx.coroutines.delay

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onOrderPlaced: (orderId: String) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val items by viewModel.items.collectAsState()
    val red = MaterialTheme.colorScheme.primary

    // Se precargan los datos del perfil solo al entrar (no al rotar la pantalla)
    var started by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!started) {
            viewModel.start()
            started = true
        }
    }

    // Tras confirmar, se muestra el éxito un momento y se pasa al mapa
    LaunchedEffect(state.stage) {
        if (state.stage == CheckoutStage.SUCCESS) {
            delay(1_600)
            state.placedOrderId?.let(onOrderPlaced)
        }
    }

    // No se puede salir mientras se procesa el pedido
    BackHandler(enabled = state.stage != CheckoutStage.EDITING) { }

    val subtotal = items.sumOf { it.subtotal }
    val fee = OrderPricing.deliveryFee(subtotal)
    val total = subtotal + fee

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                ScreenHeader(title = "Confirmar orden", onBack = onBack)

                if (items.isEmpty() && state.stage == CheckoutStage.EDITING) {
                    Text(
                        text = "Tu carrito está vacío. Agrega pizzas o bebidas desde el menú.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(20.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ---- Datos de entrega ----
                        SectionTitle("Datos de entrega")

                        if (state.profileWasIncomplete) {
                            Text(
                                text = "Aún no tienes tus datos en el perfil. Necesitamos tu nombre, " +
                                    "teléfono y dirección para entregar tu pedido.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(red.copy(alpha = 0.08f))
                                    .padding(14.dp)
                            )
                        }

                        RossoTextField(
                            value = state.name,
                            onValueChange = viewModel::onNameChange,
                            label = "Nombre completo",
                            isError = state.nameError,
                            errorText = "Escribe tu nombre"
                        )
                        RossoTextField(
                            value = state.phone,
                            onValueChange = viewModel::onPhoneChange,
                            label = "Teléfono",
                            keyboardType = KeyboardType.Phone,
                            isError = state.phoneError,
                            errorText = "Escribe un teléfono de 10 dígitos"
                        )
                        RossoTextField(
                            value = state.address,
                            onValueChange = viewModel::onAddressChange,
                            label = "Dirección de entrega",
                            singleLine = false,
                            minLines = 2,
                            isError = state.addressError,
                            errorText = "Escribe calle, número y colonia"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onSaveToProfileChange(!state.saveToProfile) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = state.saveToProfile,
                                onCheckedChange = viewModel::onSaveToProfileChange,
                                colors = CheckboxDefaults.colors(checkedColor = red)
                            )
                            Text(
                                text = "Guardar estos datos en mi perfil",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // ---- Método de pago ----
                        SectionTitle("Método de pago")
                        Text(
                            text = "Simulación: no se realiza ningún cobro ni se guardan datos de tarjetas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        PaymentMethod.entries.forEach { method ->
                            PaymentOption(
                                method = method,
                                selected = state.payment == method,
                                onClick = { viewModel.onPaymentChange(method) }
                            )
                        }
                        if (state.payment == PaymentMethod.CARD) {
                            FakeCardPreview()
                        }

                        // ---- Resumen ----
                        SectionTitle("Resumen")
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(red.copy(alpha = 0.05f))
                                .border(1.dp, red.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items.forEach { item ->
                                SummaryLine("${item.quantity}× ${item.dish.name}", formatMxn(item.subtotal))
                            }
                            HorizontalDivider(color = red.copy(alpha = 0.15f))
                            SummaryLine("Subtotal", formatMxn(subtotal))
                            SummaryLine("Envío", if (fee == 0.0) "Gratis" else formatMxn(fee))
                            if (fee > 0.0) {
                                Text(
                                    text = "Envío gratis desde ${formatMxn(OrderPricing.FREE_DELIVERY_FROM)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            SummaryLine("Total", formatMxn(total), emphasized = true)
                        }

                        PrimaryButton(
                            text = "Confirmar pedido · ${formatMxn(total)}",
                            icon = Icons.Default.Check,
                            onClick = viewModel::placeOrder,
                            enabled = items.isNotEmpty() && state.stage == CheckoutStage.EDITING,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }

            if (state.stage != CheckoutStage.EDITING) {
                ProcessingOverlay(
                    success = state.stage == CheckoutStage.SUCCESS,
                    orderId = state.placedOrderId
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 12.dp)
    )
}

@Composable
private fun SummaryLine(label: String, value: String, emphasized: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PaymentOption(method: PaymentMethod, selected: Boolean, onClick: () -> Unit) {
    val red = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) red.copy(alpha = 0.10f) else red.copy(alpha = 0.04f))
            .border(if (selected) 2.dp else 1.dp, if (selected) red else red.copy(alpha = 0.2f), shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = red)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(method.label, style = MaterialTheme.typography.titleMedium)
            Text(
                method.hint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Tarjeta decorativa: no recibe ni guarda ningún dato. */
@Composable
private fun FakeCardPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Rosso, RossoDark)))
            .padding(20.dp)
    ) {
        Text(
            text = "TARJETA DE PRUEBA",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = "••••  ••••  ••••  4242",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("ROSSO PIZZA DEMO", style = MaterialTheme.typography.labelLarge, color = Color.White)
            Text("12/30", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }
    }
}

@Composable
private fun ProcessingOverlay(success: Boolean, orderId: String?) {
    val red = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.97f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            if (success) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(red),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(56.dp)
                    )
                }
                Text(
                    text = "¡Pedido confirmado!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = red
                )
                if (orderId != null) {
                    Text(
                        text = "Pedido $orderId",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = "Te llevamos al mapa para seguirlo…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                CircularProgressIndicator(color = red, modifier = Modifier.size(56.dp))
                Text(
                    text = "Procesando pago simulado…",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "No se realiza ningún cobro real",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
