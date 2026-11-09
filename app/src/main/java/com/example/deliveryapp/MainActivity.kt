package com.example.deliveryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deliveryapp.presentation.auth.AuthScreen
import com.example.deliveryapp.presentation.auth.AuthViewModel
import com.example.deliveryapp.presentation.cart.CartScreen
import com.example.deliveryapp.presentation.home.HomeScreen
import com.example.deliveryapp.presentation.menu.MenuScreen
import com.example.deliveryapp.presentation.tracking.TrackingScreen
import com.example.deliveryapp.ui.theme.DeliveryAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeliveryAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by rememberSaveable { mutableStateOf("home") }
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // Botón/gesto "atrás" de Android: desde cualquier pantalla regresa al inicio.
    // Solo en el inicio (o sin sesión) sale de la app.
    BackHandler(enabled = isLoggedIn && currentScreen != "home") {
        currentScreen = "home"
    }

    if (!isLoggedIn) {
        AuthScreen(onLoginSuccess = { })
    } else {
        when (currentScreen) {
            "home" -> HomeScreen(
                onNavigateToMenu = { currentScreen = "menu" },
                onNavigateToCart = { currentScreen = "cart" },
                onNavigateToTracking = { currentScreen = "tracking" },
                onLogout = { authViewModel.logout() }
            )
            "menu" -> MenuScreen(
                onBack = { currentScreen = "home" }
            )
            "cart" -> CartScreen(
                onBack = { currentScreen = "home" }
            )
            "tracking" -> TrackingScreen(
                orderId = "order_123",
                onBack = { currentScreen = "home" } // <-- Pasamos el callback aquí
            )
        }
    }
}