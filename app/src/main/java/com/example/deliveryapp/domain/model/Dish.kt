package com.example.deliveryapp.domain.model

data class Dish(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String = ""
)