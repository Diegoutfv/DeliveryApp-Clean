package com.example.deliveryapp.domain.model

data class CartItem(
    val dish: Dish,
    val quantity: Int = 1
) {
    val subtotal: Double
        get() = dish.price * quantity
}