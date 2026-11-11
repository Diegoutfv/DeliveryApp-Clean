package com.example.deliveryapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deliveryapp.ui.components.PrimaryButton
import com.example.deliveryapp.ui.components.RossoTextField
import com.example.deliveryapp.ui.components.ScreenHeader

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val red = MaterialTheme.colorScheme.primary

    LaunchedEffect(Unit) { viewModel.load() }

    LaunchedEffect(state.justSaved) {
        if (state.justSaved) {
            snackbarHostState.showSnackbar("Datos guardados")
            viewModel.messageShown()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(title = "Mi perfil", onBack = onBack)

            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar con la inicial
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(red.copy(alpha = 0.12f))
                        .border(1.dp, red.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = (state.name.firstOrNull() ?: state.email.firstOrNull() ?: 'R')
                        .uppercaseChar().toString()
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.headlineLarge,
                        color = red
                    )
                }

                Text(
                    text = state.email.ifBlank { "Sin correo" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(8.dp))

                RossoTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Nombre completo"
                )
                RossoTextField(
                    value = state.phone,
                    onValueChange = viewModel::onPhoneChange,
                    label = "Teléfono",
                    keyboardType = KeyboardType.Phone
                )
                RossoTextField(
                    value = state.address,
                    onValueChange = viewModel::onAddressChange,
                    label = "Dirección de entrega",
                    singleLine = false,
                    minLines = 3
                )
                Text(
                    text = "Calle, número, colonia y alcaldía. Usaremos estos datos para tus pedidos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                PrimaryButton(
                    text = "Guardar cambios",
                    icon = Icons.Default.Check,
                    onClick = viewModel::save,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
