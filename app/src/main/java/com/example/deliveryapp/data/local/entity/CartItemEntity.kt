package com.example.deliveryapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val dishId: Int,
    val dishName: String,
    val dishPrice: Double,
    val quantity: Int
)