package com.undef.superahorro.Loza.Urieta.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * TopAppBar consistente para toda la app.
 * Centraliza los colores verdes de la marca y los patrones más comunes:
 *
 * - Título solo: SuperTopAppBar(title = "Mi pantalla")
 * - Título + back: SuperTopAppBar(title = "...", onBack = { navController.popBackStack() })
 * - Título + subtítulo: SuperTopAppBar(title = "Hola", subtitle = "Bienvenido")
 * - Con acciones a la derecha:
 *     SuperTopAppBar(title = "...", actions = { IconButton(...) { Icon(...) } })
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperTopAppBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            if (subtitle != null) {
                Column {
                    Text(title, fontWeight = FontWeight.SemiBold)
                    Text(subtitle, fontSize = 12.sp)
                }
            } else {
                Text(title)
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
