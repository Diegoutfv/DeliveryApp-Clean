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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.deliveryapp.domain.logic.DeliverySimulator
import com.example.deliveryapp.domain.logic.DeliveryStage
import com.example.deliveryapp.ui.components.CircleIconButton
import com.example.deliveryapp.ui.components.PrimaryButton
import com.example.deliveryapp.ui.components.SecondaryButton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

private fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

@Composable
fun TrackingScreen(
    orderId: String,
    onBack: () -> Unit,
    viewModel: OrderTrackingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val red = MaterialTheme.colorScheme.primary
    val uiState by viewModel.uiState.collectAsState()
    // El ViewModel puede conservar un pedido anterior un instante: solo se usa el de esta pantalla
    val current = uiState?.takeIf { it.order.id == orderId }

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

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        hasPermission = context.hasLocationPermission()
        if (hasPermission) permissionDenied = false
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(DeliverySimulator.RESTAURANT_LAT, DeliverySimulator.RESTAURANT_LNG),
            13f
        )
    }

    LaunchedEffect(orderId) { viewModel.load(orderId) }

    // Al cargar el pedido, la cámara se centra entre la sucursal y el destino
    LaunchedEffect(current?.order?.id) {
        current?.let {
            val midLat = (DeliverySimulator.RESTAURANT_LAT + it.order.destLat) / 2
            val midLng = (DeliverySimulator.RESTAURANT_LNG + it.order.destLng) / 2
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(midLat, midLng), 14f)
            )
        }
    }

    val driverMarker = rememberMarkerState()
    LaunchedEffect(current?.snapshot?.driverLat, current?.snapshot?.driverLng) {
        current?.snapshot?.let { driverMarker.position = LatLng(it.driverLat, it.driverLng) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // Solo se activa "mi ubicación" cuando el permiso fue concedido
            properties = MapProperties(isMyLocationEnabled = hasPermission)
        ) {
            current?.let { st ->
                val restaurant = LatLng(DeliverySimulator.RESTAURANT_LAT, DeliverySimulator.RESTAURANT_LNG)
                val destination = LatLng(st.order.destLat, st.order.destLng)

                Polyline(
                    points = listOf(restaurant, destination),
                    color = red.copy(alpha = 0.55f),
                    width = 10f
                )
                Marker(
                    state = rememberMarkerState(key = "restaurant", position = restaurant),
                    title = "Rosso Pizza",
                    snippet = "Sucursal",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                )
                Marker(
                    state = rememberMarkerState(key = "destination", position = destination),
                    title = "Tu dirección",
                    snippet = st.order.address,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
                Marker(
                    state = driverMarker,
                    title = DeliverySimulator.DRIVER_NAME,
                    snippet = st.snapshot.stage.label,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
            }
        }

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
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, top = 76.dp)
            )
        }

        current?.let { st ->
            TrackingInfoCard(
                state = st,
                onHome = onBack,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun TrackingInfoCard(
    state: TrackingUiState,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val red = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(28.dp)
    val snapshot = state.snapshot
    val stage = snapshot.stage

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.97f))
            .border(1.dp, red.copy(alpha = 0.25f), shape)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stage.label,
                style = MaterialTheme.typography.titleLarge,
                color = red,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = state.order.id,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LinearProgressIndicator(
            progress = { snapshot.overallProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = red,
            trackColor = red.copy(alpha = 0.15f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DeliveryStage.entries.forEach { step ->
                val reached = step.ordinal <= stage.ordinal
                Text(
                    text = when (step) {
                        DeliveryStage.PREPARING -> "Preparando"
                        DeliveryStage.ON_THE_WAY -> "En camino"
                        DeliveryStage.DELIVERED -> "Entregado"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal,
                    color = if (reached) red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = when (stage) {
                DeliveryStage.PREPARING ->
                    "Estamos horneando tu pedido · llega en ~${snapshot.etaMinutes} min"
                DeliveryStage.ON_THE_WAY ->
                    "${DeliverySimulator.DRIVER_NAME} va hacia ti · llega en ~${snapshot.etaMinutes} min"
                DeliveryStage.DELIVERED ->
                    "¡Tu pedido fue entregado! Buen provecho."
            },
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Entrega en: ${state.order.address}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (stage == DeliveryStage.DELIVERED) {
            PrimaryButton(
                text = "Volver al inicio",
                icon = Icons.Default.Home,
                onClick = onHome,
                modifier = Modifier.fillMaxWidth()
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
            .padding(20.dp)
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
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
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
