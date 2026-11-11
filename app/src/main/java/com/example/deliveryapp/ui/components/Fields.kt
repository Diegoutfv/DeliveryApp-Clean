package com.example.deliveryapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/** Campo de texto con el estilo de la marca: bordes redondeados y fondo rojo translúcido. */
@Composable
fun RossoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false,
    errorText: String? = null,
    enabled: Boolean = true
) {
    val red = MaterialTheme.colorScheme.primary
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        minLines = minLines,
        isError = isError,
        enabled = enabled,
        supportingText = if (isError && errorText != null) {
            { Text(errorText) }
        } else {
            null
        },
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = red,
            unfocusedBorderColor = red.copy(alpha = 0.25f),
            focusedContainerColor = red.copy(alpha = 0.06f),
            unfocusedContainerColor = red.copy(alpha = 0.04f),
            disabledContainerColor = red.copy(alpha = 0.03f),
            focusedLabelColor = red,
            cursorColor = red
        )
    )
}
