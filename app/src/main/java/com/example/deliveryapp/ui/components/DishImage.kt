package com.example.deliveryapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import com.example.deliveryapp.domain.model.Dish
import com.example.deliveryapp.domain.model.DishCategory
import kotlin.math.cos
import kotlin.math.sin

/**
 * Ilustraciones dibujadas con código para cada platillo del menú.
 * No usan archivos de imagen: se ven nítidas en cualquier tamaño y no pesan en la app.
 */
@Composable
fun DishImage(dish: Dish, modifier: Modifier = Modifier) =
    DishImage(dish.id, dish.category, modifier)

@Composable
fun DishImage(dishId: Int, category: DishCategory, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (dishId) {
                101 -> drawRossaClasica()
                102 -> drawPepperoni()
                103 -> drawCuatroQuesos()
                104 -> drawDiavola()
                105 -> drawHawaiana()
                106 -> drawVerde()
                201 -> drawLimonadaRosa()
                202 -> drawCola()
                203 -> drawTeDurazno()
                204 -> drawNaranjada()
                205 -> drawAguaMineral()
                206 -> drawCervezaRoja()
                else -> if (category == DishCategory.DRINK) drawLimonadaRosa() else drawPepperoni()
            }
        }
    }
}

// ---------- Utilidades ----------

private fun polar(center: Offset, radius: Float, degrees: Float, distance: Float): Offset {
    val rad = Math.toRadians(degrees.toDouble())
    return Offset(
        center.x + (cos(rad) * distance * radius).toFloat(),
        center.y + (sin(rad) * distance * radius).toFloat()
    )
}

private val Crust = Color(0xFFE3A94F)
private val CrustEdge = Color(0xFFB9772A)
private val Sauce = Color(0xFFC8341F)
private val CheeseYellow = Color(0xFFF6D56B)

/** Dibuja la masa, la salsa y (si se indica) el queso; luego los ingredientes. */
private fun DrawScope.pizza(
    cheese: Color?,
    toppings: DrawScope.(center: Offset, radius: Float) -> Unit
) {
    val r = size.minDimension / 2f
    val c = center
    drawCircle(Crust, r, c)
    drawCircle(CrustEdge, r, c, style = Stroke(width = r * 0.05f))
    drawCircle(Sauce, r * 0.84f, c)
    if (cheese != null) drawCircle(cheese, r * 0.80f, c)
    toppings(c, r)
}

// ---------- Pizzas ----------

private fun DrawScope.drawRossaClasica() = pizza(cheese = null) { c, r ->
    val mozzarella = Color(0xFFFFFBF0)
    for (i in 0 until 6) {
        drawCircle(mozzarella, r * 0.17f, polar(c, r, i * 60f + 10f, 0.50f))
        drawCircle(Color(0xFF3E9B3F), r * 0.06f, polar(c, r, i * 60f + 42f, 0.52f))
        drawCircle(Color(0xFF2E7D32), r * 0.04f, polar(c, r, i * 60f + 48f, 0.46f))
    }
    drawCircle(mozzarella, r * 0.15f, c)
    drawCircle(Color(0xFF3E9B3F), r * 0.05f, c + Offset(r * 0.05f, -r * 0.04f))
}

private fun DrawScope.drawPepperoni() = pizza(cheese = CheeseYellow) { c, r ->
    val positions = buildList {
        for (i in 0 until 7) add(polar(c, r, i * 51.4f + 10f, 0.56f))
        for (i in 0 until 3) add(polar(c, r, i * 120f + 30f, 0.24f))
    }
    positions.forEach { p ->
        drawCircle(Color(0xFFB3261E), r * 0.14f, p)
        drawCircle(Color(0xFF8E1B1B), r * 0.04f, p + Offset(r * 0.03f, -r * 0.02f))
        drawCircle(Color(0xFF8E1B1B), r * 0.03f, p + Offset(-r * 0.04f, r * 0.04f))
    }
}

private fun DrawScope.drawCuatroQuesos() = pizza(cheese = null) { c, r ->
    val topLeft = Offset(c.x - r * 0.80f, c.y - r * 0.80f)
    val area = Size(r * 1.6f, r * 1.6f)
    drawArc(Color(0xFFF6E3B0), 270f, 90f, true, topLeft, area)
    drawArc(Color(0xFFF2B84B), 0f, 90f, true, topLeft, area)
    drawArc(Color(0xFFD9E6EE), 90f, 90f, true, topLeft, area)
    drawArc(Color(0xFFF9F3E3), 180f, 90f, true, topLeft, area)
    // Motas verdes del queso azul
    for (i in 0 until 5) {
        drawCircle(Color(0xFF5C8D6B), r * 0.03f, polar(c, r, 105f + i * 14f, 0.30f + (i % 2) * 0.28f))
    }
    // Líneas entre quesos
    drawLine(Sauce, Offset(c.x, c.y - r * 0.80f), Offset(c.x, c.y + r * 0.80f), r * 0.025f)
    drawLine(Sauce, Offset(c.x - r * 0.80f, c.y), Offset(c.x + r * 0.80f, c.y), r * 0.025f)
}

private fun DrawScope.drawDiavola() = pizza(cheese = CheeseYellow) { c, r ->
    for (i in 0 until 6) {
        val p = polar(c, r, i * 60f + 20f, 0.52f)
        drawCircle(Color(0xFF8E1B1B), r * 0.16f, p)
        drawCircle(Color(0xFF5A0F0F), r * 0.05f, p + Offset(r * 0.03f, r * 0.02f))
    }
    for (i in 0 until 8) {
        val p = polar(c, r, i * 45f + 5f, 0.30f + (i % 2) * 0.28f)
        drawCircle(Color(0xFFE53935), r * 0.05f, p)
        drawCircle(Color(0xFF2E7D32), r * 0.02f, p + Offset(r * 0.02f, 0f))
    }
}

private fun DrawScope.drawHawaiana() = pizza(cheese = CheeseYellow) { c, r ->
    for (i in 0 until 6) {
        val ham = polar(c, r, i * 60f, 0.52f)
        val side = r * 0.30f
        drawRoundRect(
            Color(0xFFF4A6A0),
            topLeft = ham - Offset(side / 2, side / 2),
            size = Size(side, side),
            cornerRadius = CornerRadius(side * 0.2f)
        )
        val pine = polar(c, r, i * 60f + 30f, 0.50f)
        drawCircle(Color(0xFFFFD84A), r * 0.10f, pine)
        drawCircle(Color(0xFFFFF0A8), r * 0.04f, pine)
    }
    drawCircle(Color(0xFFFFD84A), r * 0.10f, c)
}

private fun DrawScope.drawVerde() = pizza(cheese = CheeseYellow) { c, r ->
    for (i in 0 until 4) {
        drawCircle(Color(0xFF2E9E4F), r * 0.13f, polar(c, r, i * 90f + 20f, 0.52f), style = Stroke(r * 0.045f))
        val m = polar(c, r, i * 90f + 65f, 0.52f)
        drawCircle(Color(0xFFE8D8C0), r * 0.10f, m)
        drawCircle(Color(0xFFC9B79C), r * 0.04f, m)
        drawCircle(Color(0xFF2B2B2B), r * 0.065f, polar(c, r, i * 90f + 45f, 0.28f), style = Stroke(r * 0.025f))
    }
    for (i in 0 until 5) {
        drawCircle(Color(0xFF5FB760), r * 0.07f, polar(c, r, i * 72f, 0.10f))
    }
}

// ---------- Bebidas ----------

private fun DrawScope.glass(liquid: Color, liquidTop: Float = 0.34f) {
    val s = size.minDimension
    val body = Path().apply {
        moveTo(0.28f * s, 0.20f * s)
        lineTo(0.72f * s, 0.20f * s)
        lineTo(0.66f * s, 0.90f * s)
        lineTo(0.34f * s, 0.90f * s)
        close()
    }
    drawPath(body, Color.White.copy(alpha = 0.55f))
    clipPath(body) {
        drawRect(liquid, topLeft = Offset(0f, liquidTop * s), size = Size(s, s))
        // Hielos
        val ice = Color.White.copy(alpha = 0.55f)
        drawRoundRect(ice, Offset(0.36f * s, 0.40f * s), Size(0.12f * s, 0.12f * s), CornerRadius(s * 0.02f))
        drawRoundRect(ice, Offset(0.52f * s, 0.50f * s), Size(0.12f * s, 0.12f * s), CornerRadius(s * 0.02f))
        drawRoundRect(ice, Offset(0.40f * s, 0.62f * s), Size(0.11f * s, 0.11f * s), CornerRadius(s * 0.02f))
    }
    drawPath(body, Color(0xFF9AA5AD), style = Stroke(s * 0.015f))
}

private fun DrawScope.straw(color: Color) {
    val s = size.minDimension
    drawLine(color, Offset(0.60f * s, 0.06f * s), Offset(0.50f * s, 0.80f * s), s * 0.035f, StrokeCap.Round)
}

private fun DrawScope.citrusSlice(center: Offset, radius: Float, rind: Color, flesh: Color) {
    drawCircle(rind, radius, center)
    drawCircle(flesh, radius * 0.82f, center)
    for (i in 0 until 8) {
        drawLine(
            Color.White.copy(alpha = 0.75f),
            center,
            polar(center, radius, i * 45f, 0.80f),
            radius * 0.06f
        )
    }
    drawCircle(Color.White.copy(alpha = 0.75f), radius * 0.08f, center)
}

private fun DrawScope.drawLimonadaRosa() {
    val s = size.minDimension
    glass(Color(0xFFFF7F9E))
    straw(Color(0xFFD7263D))
    citrusSlice(Offset(0.72f * s, 0.24f * s), 0.12f * s, Color(0xFFF2C230), Color(0xFFFFF59D))
}

private fun DrawScope.drawCola() {
    val s = size.minDimension
    val corner = CornerRadius(s * 0.04f)
    drawRoundRect(Color(0xFFB3261E), Offset(0.32f * s, 0.16f * s), Size(0.36f * s, 0.70f * s), corner)
    drawRoundRect(Color(0xFFD0D4D8), Offset(0.32f * s, 0.12f * s), Size(0.36f * s, 0.06f * s), corner)
    drawRoundRect(Color(0xFFD0D4D8), Offset(0.32f * s, 0.84f * s), Size(0.36f * s, 0.05f * s), corner)
    val wave = Path().apply {
        moveTo(0.32f * s, 0.45f * s)
        quadraticBezierTo(0.50f * s, 0.36f * s, 0.68f * s, 0.45f * s)
        lineTo(0.68f * s, 0.56f * s)
        quadraticBezierTo(0.50f * s, 0.47f * s, 0.32f * s, 0.56f * s)
        close()
    }
    drawPath(wave, Color.White)
    drawRect(Color.White.copy(alpha = 0.18f), Offset(0.36f * s, 0.20f * s), Size(0.04f * s, 0.62f * s))
    drawCircle(Color(0xFF9AA5AD), s * 0.025f, Offset(0.50f * s, 0.14f * s))
}

private fun DrawScope.drawTeDurazno() {
    val s = size.minDimension
    glass(Color(0xFFF4A259))
    straw(Color(0xFF2E7D32))
    // Rebanada de durazno
    val c = Offset(0.30f * s, 0.26f * s)
    drawCircle(Color(0xFFFF8A65), s * 0.10f, c)
    drawCircle(Color(0xFFFFCCBC), s * 0.075f, c)
    drawCircle(Color(0xFFD7845F), s * 0.03f, c)
    // Menta
    drawOval(Color(0xFF43A047), Offset(0.42f * s, 0.10f * s), Size(0.10f * s, 0.06f * s))
    drawOval(Color(0xFF2E7D32), Offset(0.48f * s, 0.07f * s), Size(0.10f * s, 0.06f * s))
}

private fun DrawScope.drawNaranjada() {
    val s = size.minDimension
    glass(Color(0xFFFFA928))
    straw(Color(0xFFD7263D))
    citrusSlice(Offset(0.72f * s, 0.24f * s), 0.12f * s, Color(0xFFFB8C00), Color(0xFFFFD180))
}

private fun DrawScope.drawAguaMineral() {
    val s = size.minDimension
    val water = Color(0xFFBFE3F5)
    drawRoundRect(water, Offset(0.34f * s, 0.30f * s), Size(0.32f * s, 0.60f * s), CornerRadius(s * 0.08f))
    drawRoundRect(water, Offset(0.43f * s, 0.14f * s), Size(0.14f * s, 0.22f * s), CornerRadius(s * 0.03f))
    drawRoundRect(Color(0xFF1E88E5), Offset(0.42f * s, 0.08f * s), Size(0.16f * s, 0.07f * s), CornerRadius(s * 0.02f))
    drawRoundRect(
        Color(0xFF7FB8D6), Offset(0.34f * s, 0.30f * s), Size(0.32f * s, 0.60f * s),
        CornerRadius(s * 0.08f), style = Stroke(s * 0.015f)
    )
    drawRect(Color.White.copy(alpha = 0.85f), Offset(0.34f * s, 0.52f * s), Size(0.32f * s, 0.16f * s))
    drawRect(Color(0xFF1E88E5), Offset(0.34f * s, 0.58f * s), Size(0.32f * s, 0.03f * s))
    listOf(0.40f to 0.40f, 0.52f to 0.76f, 0.58f to 0.45f, 0.44f to 0.80f).forEach { (x, y) ->
        drawCircle(Color.White.copy(alpha = 0.8f), s * 0.018f, Offset(x * s, y * s))
    }
}

private fun DrawScope.drawCervezaRoja() {
    val s = size.minDimension
    val corner = CornerRadius(s * 0.04f)
    // Asa
    drawArc(
        Color(0xFF9AA5AD), -90f, 180f, false,
        Offset(0.54f * s, 0.34f * s), Size(0.24f * s, 0.34f * s),
        style = Stroke(s * 0.035f)
    )
    drawRoundRect(Color.White.copy(alpha = 0.55f), Offset(0.28f * s, 0.26f * s), Size(0.38f * s, 0.62f * s), corner)
    drawRoundRect(Color(0xFFB5451B), Offset(0.30f * s, 0.38f * s), Size(0.34f * s, 0.48f * s), corner)
    drawRoundRect(
        Color(0xFF9AA5AD), Offset(0.28f * s, 0.26f * s), Size(0.38f * s, 0.62f * s),
        corner, style = Stroke(s * 0.015f)
    )
    // Espuma
    drawRoundRect(Color(0xFFFFF4DC), Offset(0.27f * s, 0.20f * s), Size(0.40f * s, 0.17f * s), CornerRadius(s * 0.07f))
    drawCircle(Color(0xFFFFF4DC), s * 0.05f, Offset(0.36f * s, 0.18f * s))
    drawCircle(Color(0xFFFFF4DC), s * 0.06f, Offset(0.48f * s, 0.16f * s))
    drawCircle(Color(0xFFFFF4DC), s * 0.045f, Offset(0.58f * s, 0.19f * s))
    listOf(0.38f to 0.60f, 0.50f to 0.70f, 0.55f to 0.52f).forEach { (x, y) ->
        drawCircle(Color(0xFFE8935A), s * 0.014f, Offset(x * s, y * s))
    }
}
