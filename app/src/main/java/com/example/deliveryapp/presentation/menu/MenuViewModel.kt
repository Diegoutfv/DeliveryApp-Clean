package com.example.deliveryapp.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.domain.model.Dish
import com.example.deliveryapp.domain.repository.DeliveryRepository
import com.example.deliveryapp.domain.usecase.AddToCartUseCase
import com.example.deliveryapp.domain.usecase.GetCartItemsUseCase
import com.example.deliveryapp.domain.usecase.GetDishesUseCase
import com.example.deliveryapp.domain.usecase.RemoveFromCartUseCase
import com.example.deliveryapp.domain.usecase.UpdateQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getDishesUseCase: GetDishesUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
    private val repository: DeliveryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        observeDishes()
        observeCart()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.loadDishesFromApi()
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Error: ${e.message}") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun observeDishes() {
        viewModelScope.launch {
            getDishesUseCase().collect { dishes ->
                _uiState.update { it.copy(dishes = dishes) }
            }
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            getCartItemsUseCase().collect { items ->
                val total = items.sumOf { it.subtotal }
                _uiState.update { it.copy(cartItems = items, total = total) }
            }
        }
    }

    fun onAddDish(dish: Dish) {
        viewModelScope.launch {
            addToCartUseCase(dish)
            _uiState.update { it.copy(message = "${dish.name} agregado") }
        }
    }

    fun onIncrease(dishId: Int) {
        viewModelScope.launch {
            updateQuantityUseCase.increase(dishId)
        }
    }

    fun onDecrease(dishId: Int) {
        viewModelScope.launch {
            updateQuantityUseCase.decrease(dishId)
        }
    }

    fun onRemove(dishId: Int) {
        viewModelScope.launch {
            removeFromCartUseCase(dishId)
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }
}