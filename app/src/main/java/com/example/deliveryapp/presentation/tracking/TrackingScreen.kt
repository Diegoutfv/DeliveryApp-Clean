package com.example.deliveryapp.presentation.tracking

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import com.example.deliveryapp.ui.components.CircleIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun TrackingScreen(
    orderId: String,
    onBack: () -> Unit, // <-- Agregado para el botón de volver
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val order by viewModel.orderState.collectAsState()

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
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    LatLng(order.lat, order.lng),
                    16f
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
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
    }
}