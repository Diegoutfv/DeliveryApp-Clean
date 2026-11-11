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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.deliveryapp.presentation.checkout.CheckoutScreen
import com.example.deliveryapp.presentation.home.HomeScreen
import com.example.deliveryapp.presentation.menu.MenuScreen
import com.example.deliveryapp.presentation.orders.OrdersScreen
import com.example.deliveryapp.presentation.orders.OrdersViewModel
import com.example.deliveryapp.presentation.profile.ProfileScreen
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
    var trackingOrderId by rememberSaveable { mutableStateOf("") }

    val authViewModel: AuthViewModel = hiltViewModel()
    val ordersViewModel: OrdersViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val orders by ordersViewModel.orders.collectAsState()

    // Botón/gesto "atrás" de Android: del pago vuelve al carrito; de las demás pantallas
    // al inicio. Solo en el inicio (o sin sesión) sale de la app.
    BackHandler(enabled = isLoggedIn && currentScreen != "home") {
        currentScreen = if (currentScreen == "checkout") "cart" else "home"
    }

    if (!isLoggedIn) {
        AuthScreen(onLoginSuccess = { })
    } else {
        when (currentScreen) {
            "home" -> HomeScreen(
                hasOrders = orders.isNotEmpty(),
                onNavigateToMenu = { currentScreen = "menu" },
                onNavigateToCart = { currentScreen = "cart" },
                onNavigateToTracking = {
                    // Rastrea el pedido más reciente
                    orders.firstOrNull()?.let {
                        trackingOrderId = it.id
                        currentScreen = "tracking"
                    }
                },
                onNavigateToOrders = { currentScreen = "orders" },
                onNavigateToProfile = { currentScreen = "profile" },
                onLogout = { authViewModel.logout() }
            )
            "menu" -> MenuScreen(
                onBack = { currentScreen = "home" },
                onOpenCart = { currentScreen = "cart" }
            )
            "cart" -> CartScreen(
                onBack = { currentScreen = "home" },
                onCheckout = { currentScreen = "checkout" }
            )
            "checkout" -> CheckoutScreen(
                onBack = { currentScreen = "cart" },
                onOrderPlaced = { orderId ->
                    // Pedido procesado: se muestra el mapa para seguirlo
                    trackingOrderId = orderId
                    currentScreen = "tracking"
                }
            )
            "orders" -> OrdersScreen(
                onBack = { currentScreen = "home" },
                onTrack = { orderId ->
                    trackingOrderId = orderId
                    currentScreen = "tracking"
                }
            )
            "profile" -> ProfileScreen(
                onBack = { currentScreen = "home" }
            )
            "tracking" -> if (trackingOrderId.isNotEmpty()) {
                TrackingScreen(
                    orderId = trackingOrderId,
                    onBack = { currentScreen = "home" }
                )
            } else {
                // Sin pedido que seguir (por ejemplo, al restaurar la app): vuelve al inicio
                LaunchedEffect(Unit) { currentScreen = "home" }
            }
        }
    }
}
