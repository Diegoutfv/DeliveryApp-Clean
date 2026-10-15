package com.example.deliveryapp.domain.repository

import com.example.deliveryapp.domain.model.CartItem
import com.example.deliveryapp.domain.model.Dish
import kotlinx.coroutines.flow.Flow

interface DeliveryRepository {

    // Menú
    fun getDishes(): Flow<List<Dish>>
    suspend fun loadDishesFromApi()

    // Carrito
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(dish: Dish)
    suspend fun increaseQuantity(dishId: Int)
    suspend fun decreaseQuantity(dishId: Int)
    suspend fun removeFromCart(dishId: Int)
    suspend fun clearCart()
}