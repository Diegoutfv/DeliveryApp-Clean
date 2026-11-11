package com.example.deliveryapp.data.repository

import com.example.deliveryapp.data.local.dao.OrderDao
import com.example.deliveryapp.data.local.entity.OrderEntity
import com.example.deliveryapp.domain.model.Order
import com.example.deliveryapp.domain.model.PaymentMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao
) {
    fun observeOrders(): Flow<List<Order>> =
        orderDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeOrder(id: String): Flow<Order?> =
        orderDao.observeById(id).map { it?.toDomain() }

    suspend fun insert(order: Order) = orderDao.insert(order.toEntity())
}

private fun OrderEntity.toDomain() = Order(
    id = id,
    createdAt = createdAt,
    itemsSummary = itemsSummary,
    subtotal = subtotal,
    deliveryFee = deliveryFee,
    total = total,
    customerName = customerName,
    phone = phone,
    address = address,
    paymentMethod = runCatching { PaymentMethod.valueOf(paymentMethod) }
        .getOrDefault(PaymentMethod.CARD),
    destLat = destLat,
    destLng = destLng
)

private fun Order.toEntity() = OrderEntity(
    id = id,
    createdAt = createdAt,
    itemsSummary = itemsSummary,
    subtotal = subtotal,
    deliveryFee = deliveryFee,
    total = total,
    customerName = customerName,
    phone = phone,
    address = address,
    paymentMethod = paymentMethod.name,
    destLat = destLat,
    destLng = destLng
)
