package com.example.deliveryapp.domain.usecase

import com.example.deliveryapp.domain.model.Dish
import com.example.deliveryapp.domain.repository.DeliveryRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(dish: Dish) {
        repository.addToCart(dish)
    }
}