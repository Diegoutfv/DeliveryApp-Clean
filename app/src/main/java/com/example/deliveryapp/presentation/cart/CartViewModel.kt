package com.example.deliveryapp.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.domain.model.CartItem
import com.example.deliveryapp.domain.model.Dish
import com.example.deliveryapp.domain.usecase.AddToCartUseCase
import com.example.deliveryapp.domain.usecase.GetCartItemsUseCase
import com.example.deliveryapp.domain.usecase.RemoveFromCartUseCase
import com.example.deliveryapp.domain.usecase.UpdateQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val addToCartUseCase: AddToCartUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    init {
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            getCartItemsUseCase().collect { items ->
                _cartItems.value = items
                _total.value = items.sumOf { it.subtotal }
            }
        }
    }

    fun onAddDish(dish: Dish) {
        viewModelScope.launch { addToCartUseCase(dish) }
    }

    fun onIncrease(dishId: Int) {
        viewModelScope.launch { updateQuantityUseCase.increase(dishId) }
    }

    fun onDecrease(dishId: Int) {
        viewModelScope.launch { updateQuantityUseCase.decrease(dishId) }
    }

    fun onRemove(dishId: Int) {
        viewModelScope.launch { removeFromCartUseCase(dishId) }
    }
}