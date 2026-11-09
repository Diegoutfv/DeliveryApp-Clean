package com.example.deliveryapp.ui.components

import java.util.Locale

/** Formatea un monto en pesos mexicanos, por ejemplo "$1,249.00 MXN". */
fun formatMxn(amount: Double): String =
    "$" + String.format(Locale.US, "%,.2f", amount) + " MXN"
