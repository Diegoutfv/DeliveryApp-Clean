package com.example.deliveryapp.presentation.menu

import com.example.deliveryapp.domain.model.CartItem
import com.example.deliveryapp.domain.model.Dish

data class MenuUiState(
    val isLoading: Boolean = false,
    val dishes: List<Dish> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val message: String? = null
)