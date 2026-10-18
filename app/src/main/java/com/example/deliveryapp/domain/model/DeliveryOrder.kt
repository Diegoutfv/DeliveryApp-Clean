package com.example.deliveryapp.domain.model

data class DeliveryOrder(
    val orderId: String = "",
    val driverName: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val status: String = ""
)