package com.example.deliveryapp.presentation.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.data.repository.TrackingRepository
import com.example.deliveryapp.domain.model.DeliveryOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val repository: TrackingRepository
) : ViewModel() {

    private val _orderState = MutableStateFlow(DeliveryOrder())
    val orderState: StateFlow<DeliveryOrder> = _orderState.asStateFlow()

    fun trackOrder(orderId: String) {
        viewModelScope.launch {
            repository.getLiveLocation(orderId).collect { order ->
                _orderState.value = order
            }
        }
    }
}