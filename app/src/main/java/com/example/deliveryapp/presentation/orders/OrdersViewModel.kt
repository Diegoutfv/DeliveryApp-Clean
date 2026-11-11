package com.example.deliveryapp.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.data.repository.OrderRepository
import com.example.deliveryapp.domain.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    repository: OrderRepository
) : ViewModel() {

    /** Pedidos del más reciente al más antiguo. */
    val orders: StateFlow<List<Order>> = repository.observeOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
