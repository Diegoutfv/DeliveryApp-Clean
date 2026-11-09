package com.example.deliveryapp.domain.model

enum class DishCategory(val label: String) {
    PIZZA("Pizzas"),
    DRINK("Bebidas")
}

data class Dish(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String = "",
    val category: DishCategory = DishCategory.PIZZA
)