package com.example.deliveryapp.domain.usecase

import com.example.deliveryapp.domain.repository.DeliveryRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(dishId: Int) {
        repository.removeFromCart(dishId)
    }
}