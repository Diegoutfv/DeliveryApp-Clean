package com.example.deliveryapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Rosso,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFDE7EA),
    onPrimaryContainer = RossoDark,
    secondary = RossoDark,
    onSecondary = Color.White,
    background = Crema,
    onBackground = Tinta,
    surface = Crema,
    onSurface = Tinta,
    surfaceVariant = Color(0xFFF7EEEE),
    onSurfaceVariant = TintaSuave,
    outline = Color(0xFFD9C7C7),
    error = RossoDark
)

private val DarkColorScheme = darkColorScheme(
    primary = RossoLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4A1A1E),
    onPrimaryContainer = Color(0xFFFFD9DB),
    secondary = Rosso,
    onSecondary = Color.White,
    background = NocheCalida,
    onBackground = NocheTexto,
    surface = NocheCalida,
    onSurface = NocheTexto,
    surfaceVariant = NocheSuperficie,
    onSurfaceVariant = NocheTextoSuave,
    outline = Color(0xFF4A3F3F),
    error = RossoLight
)

@Composable
fun DeliveryAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Sin colores dinámicos: la identidad de marca es siempre roja
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
