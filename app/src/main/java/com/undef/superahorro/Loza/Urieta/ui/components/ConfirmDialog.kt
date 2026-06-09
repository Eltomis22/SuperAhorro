package com.undef.superahorro.Loza.Urieta.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.undef.superahorro.Loza.Urieta.R

/**
 * Diálogo de confirmación reutilizable. Muestra un título, un mensaje opcional
 * y dos botones: confirmar (rojo por defecto, para acciones destructivas) y cancelar.
 *
 * Ej:
 *   ConfirmDialog(
 *       title = "¿Eliminar esto?",
 *       confirmText = "Eliminar",
 *       onConfirm = { ... },
 *       onDismiss = { showDialog = false }
 *   )
 */
@Composable
fun ConfirmDialog(
    title: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    message: String? = null,
    confirmColor: Color = MaterialTheme.colorScheme.error,
    cancelText: String = stringResource(R.string.action_cancel)
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = if (message != null) {
            { Text(message) }
        } else null,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText, color = confirmColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        }
    )
}
