package com.example.deliveryapp.presentation.tracking

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.deliveryapp.ui.components.CircleIconButton
import com.example.deliveryapp.ui.components.PrimaryButton
import com.example.deliveryapp.ui.components.SecondaryButton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

@Composable
fun TrackingScreen(
    orderId: String,
    onBack: () -> Unit,
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val order by viewModel.orderState.collectAsState()

    // Permiso de ubicación: se pide en tiempo de ejecución, con explicación previa
    var hasPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    var permissionDenied by remember { mutableStateOf(false) }
    var rationaleDismissed by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasPermission = results.values.any { it }
        permissionDenied = !hasPermission
    }

    // Al volver de los ajustes del sistema, se revisa de nuevo el permiso
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        hasPermission = context.hasLocationPermission()
        if (hasPermission) permissionDenied = false
    }

    // Posición inicial de la cámara (CDMX)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(19.4326, -99.1332), 12f)
    }

    LaunchedEffect(orderId) {
        viewModel.trackOrder(orderId)
    }

    // Animar la cámara cuando cambien las coordenadas
    LaunchedEffect(order.lat, order.lng) {
        if (order.lat != 0.0 && order.lng != 0.0) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(order.lat, order.lng),
                    16f
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // Solo se activa "mi ubicación" cuando el permiso fue concedido
            properties = MapProperties(isMyLocationEnabled = hasPermission)
        ) {
            if (order.lat != 0.0 && order.lng != 0.0) {
                Marker(
                    state = MarkerState(position = LatLng(order.lat, order.lng)),
                    title = order.driverName.ifEmpty { "Repartidor" },
                    snippet = "Estado: ${order.status.ifEmpty { "Desconocido" }}"
                )
            }
        }

        // Botón para regresar del mapa al Home
        CircleIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Volver",
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background, CircleShape)
        )

        if (!hasPermission && !rationaleDismissed) {
            LocationPermissionCard(
                denied = permissionDenied,
                onAllow = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                onOpenSettings = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                },
                onDismiss = { rationaleDismissed = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun LocationPermissionCard(
    denied: Boolean,
    onAllow: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val red = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(28.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
            .border(1.dp, red.copy(alpha = 0.25f), shape)
            .padding(24.dp)
    ) {
        Text(
            text = "Mostrar mi ubicación",
            style = MaterialTheme.typography.titleLarge,
            color = red
        )
        Text(
            text = if (denied) {
                "Sin el permiso no podemos mostrar dónde estás en el mapa. " +
                    "Puedes activarlo en los ajustes de la app."
            } else {
                "Rosso Pizza usa tu ubicación solo mientras el mapa está abierto, " +
                    "para mostrarte junto a tu pedido."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
        if (denied) {
            SecondaryButton(
                text = "Abrir ajustes",
                icon = Icons.Default.LocationOn,
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            PrimaryButton(
                text = "Permitir ubicación",
                icon = Icons.Default.LocationOn,
                onClick = onAllow,
                modifier = Modifier.fillMaxWidth()
            )
        }
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Ahora no")
        }
    }
}
