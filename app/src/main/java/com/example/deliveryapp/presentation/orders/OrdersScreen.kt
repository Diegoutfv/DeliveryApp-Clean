package com.example.deliveryapp.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deliveryapp.domain.logic.DeliverySimulator
import com.example.deliveryapp.domain.logic.DeliveryStage
import com.example.deliveryapp.domain.model.Order
import com.example.deliveryapp.ui.components.ScreenHeader
import com.example.deliveryapp.ui.components.SecondaryButton
import com.example.deliveryapp.ui.components.formatMxn
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    onBack: () -> Unit,
    onTrack: (orderId: String) -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val red = MaterialTheme.colorScheme.primary

    // Reloj que se actualiza cada segundo para refrescar el estado de cada pedido
    val now by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            delay(1_000)
            value = System.currentTimeMillis()
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScreenHeader(title = "Mis pedidos", onBack = onBack)

            if (orders.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = red.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Todavía no tienes pedidos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = "Cuando hagas uno, aparecerá aquí.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        OrderCard(order = order, now = now, onTrack = { onTrack(order.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, now: Long, onTrack: () -> Unit) {
    val red = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(24.dp)
    val snapshot = DeliverySimulator.snapshot(order, now)
    val delivered = snapshot.stage == DeliveryStage.DELIVERED
    val date = SimpleDateFormat("d MMM · HH:mm", Locale("es", "MX")).format(Date(order.createdAt))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(red.copy(alpha = 0.05f))
            .border(1.dp, red.copy(alpha = 0.15f), shape)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Pedido ${order.id}", style = MaterialTheme.typography.titleMedium)
            Text(
                text = snapshot.stage.label,
                style = MaterialTheme.typography.labelLarge,
                color = if (delivered) MaterialTheme.colorScheme.onSurfaceVariant else red,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (delivered) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                        else red.copy(alpha = 0.12f)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = order.itemsSummary, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "Total ${formatMxn(order.total)} · ${order.paymentMethod.label}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SecondaryButton(
            text = if (delivered) "Ver recorrido" else "Rastrear pedido",
            icon = Icons.Default.LocationOn,
            onClick = onTrack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
