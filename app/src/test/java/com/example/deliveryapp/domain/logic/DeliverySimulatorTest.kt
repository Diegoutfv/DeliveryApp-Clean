package com.example.deliveryapp.domain.logic

import com.example.deliveryapp.domain.model.Order
import com.example.deliveryapp.domain.model.OrderPricing
import com.example.deliveryapp.domain.model.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeliverySimulatorTest {

    private val start = 1_000_000L
    private val order = Order(
        id = "RP-000001",
        createdAt = start,
        itemsSummary = "1x Rossa Clásica",
        subtotal = 189.0,
        deliveryFee = 29.0,
        total = 218.0,
        customerName = "Ana",
        phone = "5512345678",
        address = "Av. Reforma 100",
        paymentMethod = PaymentMethod.CARD,
        destLat = DeliverySimulator.FALLBACK_DEST_LAT,
        destLng = DeliverySimulator.FALLBACK_DEST_LNG
    )

    @Test
    fun `al inicio el pedido se esta preparando y el repartidor esta en el restaurante`() {
        val snap = DeliverySimulator.snapshot(order, start)

        assertEquals(DeliveryStage.PREPARING, snap.stage)
        assertEquals(DeliverySimulator.RESTAURANT_LAT, snap.driverLat, 1e-9)
        assertEquals(DeliverySimulator.RESTAURANT_LNG, snap.driverLng, 1e-9)
        assertEquals(30, snap.etaMinutes)
    }

    @Test
    fun `despues de preparar el pedido va en camino y se acerca al destino`() {
        val early = DeliverySimulator.snapshot(order, start + DeliverySimulator.PREPARING_MS + 1_000)
        val late = DeliverySimulator.snapshot(order, start + DeliverySimulator.PREPARING_MS + 40_000)

        assertEquals(DeliveryStage.ON_THE_WAY, early.stage)
        assertTrue(late.travelProgress > early.travelProgress)
        assertTrue(late.etaMinutes < early.etaMinutes)
    }

    @Test
    fun `al terminar el recorrido queda entregado en el destino`() {
        val total = DeliverySimulator.PREPARING_MS + DeliverySimulator.TRAVEL_MS
        val snap = DeliverySimulator.snapshot(order, start + total + 5_000)

        assertEquals(DeliveryStage.DELIVERED, snap.stage)
        assertEquals(order.destLat, snap.driverLat, 1e-9)
        assertEquals(order.destLng, snap.driverLng, 1e-9)
        assertEquals(0, snap.etaMinutes)
    }

    @Test
    fun `el envio es gratis desde 400 pesos`() {
        assertEquals(29.0, OrderPricing.deliveryFee(399.0), 0.0)
        assertEquals(0.0, OrderPricing.deliveryFee(400.0), 0.0)
    }
}
