package com.example.deliveryapp.presentation.cart

import com.example.deliveryapp.ui.components.formatMxn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deliveryapp.presentation.menu.components.CartItemRow
import com.example.deliveryapp.ui.components.PrimaryButton
import com.example.deliveryapp.ui.components.ScreenHeader

@Composable
fun CartScreen(
    onBack: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val total by viewModel.total.collectAsState()
    val red = MaterialTheme.colorScheme.primary

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScreenHeader(title = "Mi carrito", onBack = onBack)

            if (cartItems.isEmpty()) {
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
                    Box(Modifier.padding(8.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Agrega una pizza o una bebida del menú",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    items(cartItems, key = { it.dish.id }) { item ->
                        CartItemRow(
                            item = item,
                            onIncrease = { viewModel.onIncrease(item.dish.id) },
                            onDecrease = { viewModel.onDecrease(item.dish.id) },
                            onRemove = { viewModel.onRemove(item.dish.id) }
                        )
                    }
                }

                HorizontalDivider(color = red.copy(alpha = 0.15f))

                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatMxn(total),
                            style = MaterialTheme.typography.headlineMedium,
                            color = red
                        )
                    }

                    Box(Modifier.padding(6.dp))

                    PrimaryButton(
                        text = "Confirmar orden",
                        onClick = { /* TODO: Checkout */ },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
