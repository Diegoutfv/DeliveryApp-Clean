package com.example.deliveryapp.domain.logic

import com.example.deliveryapp.domain.model.Order
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class DeliveryStage(val label: String) {
    PREPARING("Preparando tu pedido"),
    ON_THE_WAY("En camino"),
    DELIVERED("Entregado")
}

data class DeliverySnapshot(
    val stage: DeliveryStage,
    val driverLat: Double,
    val driverLng: Double,
    /** Avance del recorrido del repartidor, de 0 a 1. */
    val travelProgress: Float,
    /** Avance total del pedido, de 0 a 1. */
    val overallProgress: Float,
    /** Minutos que faltan, en tiempo "simulado". */
    val etaMinutes: Int
)

/**
 * Simula el reparto sin servidor: el estado y la posición del repartidor
 * dependen solo del tiempo transcurrido desde que se creó el pedido.
 * 15 s preparando + 60 s de recorrido = 30 "minutos" simulados.
 */
object DeliverySimulator {
    const val RESTAURANT_LAT = 19.4270
    const val RESTAURANT_LNG = -99.1676
    const val FALLBACK_DEST_LAT = 19.4390
    const val FALLBACK_DEST_LNG = -99.1530
    const val DRIVER_NAME = "Carlos M."

    const val PREPARING_MS = 15_000L
    const val TRAVEL_MS = 60_000L
    private const val TOTAL_MS = PREPARING_MS + TRAVEL_MS
    private const val MS_PER_SIM_MINUTE = TOTAL_MS / 30

    fun snapshot(order: Order, now: Long): DeliverySnapshot {
        val elapsed = (now - order.createdAt).coerceAtLeast(0L)
        val stage = when {
            elapsed < PREPARING_MS -> DeliveryStage.PREPARING
            elapsed < TOTAL_MS -> DeliveryStage.ON_THE_WAY
            else -> DeliveryStage.DELIVERED
        }
        val travel = ((elapsed - PREPARING_MS).toFloat() / TRAVEL_MS).coerceIn(0f, 1f)
        val remaining = (TOTAL_MS - elapsed).coerceAtLeast(0L)

        return DeliverySnapshot(
            stage = stage,
            driverLat = lerp(RESTAURANT_LAT, order.destLat, travel),
            driverLng = lerp(RESTAURANT_LNG, order.destLng, travel),
            travelProgress = travel,
            overallProgress = (elapsed.toFloat() / TOTAL_MS).coerceIn(0f, 1f),
            etaMinutes = ceil(remaining.toDouble() / MS_PER_SIM_MINUTE).toInt()
        )
    }

    private fun lerp(from: Double, to: Double, t: Float): Double = from + (to - from) * t

    /** Distancia aproximada entre dos puntos, en kilómetros (fórmula de Haversine). */
    fun distanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return earthRadiusKm * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
