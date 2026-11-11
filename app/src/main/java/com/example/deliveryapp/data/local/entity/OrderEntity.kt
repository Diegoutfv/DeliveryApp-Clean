package com.example.deliveryapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val createdAt: Long,
    val itemsSummary: String,
    val subtotal: Double,
    val deliveryFee: Double,
    val total: Double,
    val customerName: String,
    val phone: String,
    val address: String,
    val paymentMethod: String,
    val destLat: Double,
    val destLng: Double
)
