package com.example.deliveryapp.presentation.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deliveryapp.domain.model.DishCategory
import com.example.deliveryapp.presentation.cart.CartViewModel
import com.example.deliveryapp.presentation.menu.components.DishItem
import com.example.deliveryapp.ui.components.ScreenHeader
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(
    onBack: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val dishes by viewModel.dishes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selected by rememberSaveable { mutableStateOf(DishCategory.PIZZA.name) }
    val selectedCategory = DishCategory.valueOf(selected)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScreenHeader(title = "Nuestro menú", onBack = onBack)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                CategoryChip(
                    label = DishCategory.PIZZA.label,
                    icon = Icons.Default.LocalPizza,
                    selected = selectedCategory == DishCategory.PIZZA,
                    onClick = { selected = DishCategory.PIZZA.name }
                )
                Box(Modifier.width(10.dp))
                CategoryChip(
                    label = DishCategory.DRINK.label,
                    icon = Icons.Default.LocalDrink,
                    selected = selectedCategory == DishCategory.DRINK,
                    onClick = { selected = DishCategory.DRINK.name }
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    items(
                        items = dishes.filter { it.category == selectedCategory },
                        key = { it.id }
                    ) { dish ->
                        DishItem(
                            dish = dish,
                            onAddClick = {
                                cartViewModel.onAddDish(dish)
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("${dish.name} agregada al carrito")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val red = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(24.dp)
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else red

    Row(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) red else red.copy(alpha = 0.08f))
            .border(1.dp, if (selected) red else red.copy(alpha = 0.25f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.width(20.dp))
        Box(Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}
