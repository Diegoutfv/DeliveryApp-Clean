package com.example.deliveryapp.domain.usecase

import com.example.deliveryapp.domain.repository.DeliveryRepository
import javax.inject.Inject

class UpdateQuantityUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend fun increase(dishId: Int) {
        repository.increaseQuantity(dishId)
    }

    suspend fun decrease(dishId: Int) {
        repository.decreaseQuantity(dishId)
    }
}