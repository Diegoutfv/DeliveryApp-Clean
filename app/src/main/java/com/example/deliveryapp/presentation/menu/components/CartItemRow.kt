package com.example.deliveryapp.presentation.menu.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.deliveryapp.domain.model.CartItem
import com.example.deliveryapp.ui.components.QuantityStepper

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val red = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(22.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(shape)
            .background(red.copy(alpha = 0.05f))
            .border(1.dp, red.copy(alpha = 0.15f), shape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.dish.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$" + String.format("%.2f", item.subtotal),
                style = MaterialTheme.typography.bodyLarge,
                color = red
            )
        }

        QuantityStepper(
            quantity = item.quantity,
            onIncrease = onIncrease,
            onDecrease = onDecrease
        )

        Spacer(Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Eliminar",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onRemove)
                .padding(6.dp)
        )
    }
}
