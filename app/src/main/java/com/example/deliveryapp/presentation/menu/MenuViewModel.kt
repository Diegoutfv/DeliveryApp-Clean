package com.example.deliveryapp.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.domain.model.Dish
import com.example.deliveryapp.domain.repository.DeliveryRepository
import com.example.deliveryapp.domain.usecase.GetDishesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getDishesUseCase: GetDishesUseCase,
    private val repository: DeliveryRepository
) : ViewModel() {

    private val _dishes = MutableStateFlow<List<Dish>>(emptyList())
    val dishes: StateFlow<List<Dish>> = _dishes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDishes()
    }

    private fun loadDishes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.loadDishesFromApi()
            } catch (e: Exception) {
                // Manejar error si es necesario
            }
            _isLoading.value = false // <-- Apagamos el spinner ANTES de empezar a escuchar

            getDishesUseCase().collect { dishes ->
                _dishes.value = dishes
            }
        }
    }
}