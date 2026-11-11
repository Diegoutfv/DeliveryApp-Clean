package com.example.deliveryapp.domain.model

/** Métodos de pago SIMULADOS: no se procesa ningún cobro ni se guardan datos de tarjetas. */
enum class PaymentMethod(val label: String, val hint: String) {
    CARD("Tarjeta de crédito o débito", "Pago simulado con tarjeta de prueba"),
    CASH("Efectivo al recibir", "Paga al repartidor cuando llegue"),
    WALLET("Billetera digital", "Pago simulado desde tu saldo")
}

/** Un pedido ya confirmado. El estado (preparando, en camino, entregado) se calcula con el tiempo. */
data class Order(
    val id: String,
    val createdAt: Long,
    val itemsSummary: String,
    val subtotal: Double,
    val deliveryFee: Double,
    val total: Double,
    val customerName: String,
    val phone: String,
    val address: String,
    val paymentMethod: PaymentMethod,
    val destLat: Double,
    val destLng: Double
)

/** Datos del usuario para entregas. */
data class UserProfile(
    val name: String = "",
    val phone: String = "",
    val address: String = ""
) {
    val isComplete: Boolean
        get() = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()
}

/** Reglas de precio del envío. */
object OrderPricing {
    const val DELIVERY_FEE = 29.0
    const val FREE_DELIVERY_FROM = 400.0

    fun deliveryFee(subtotal: Double): Double =
        if (subtotal >= FREE_DELIVERY_FROM || subtotal <= 0.0) 0.0 else DELIVERY_FEE
}
