package com.example.deliveryapp.domain.usecase

import com.example.deliveryapp.domain.model.Dish
import com.example.deliveryapp.domain.repository.DeliveryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDishesUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    operator fun invoke(): Flow<List<Dish>> {
        return repository.getDishes()
    }
}