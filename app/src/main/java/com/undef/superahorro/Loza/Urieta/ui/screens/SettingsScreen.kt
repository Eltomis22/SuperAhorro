package com.undef.superahorro.Loza.Urieta.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.ui.AppSettings
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar

/**
 * Pantalla de configuración de la app.
 * - Modo oscuro: cambia la paleta del theme inmediatamente.
 * - Idioma: re-aplica el Locale y stringResource() pasa a leer del XML correcto.
 * - Notificaciones / Biometría: por ahora solo guardan estado (placeholder).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {

    // Conectado al singleton global → cambios se reflejan en toda la app
    val darkMode = AppSettings.darkMode
    val notifications = AppSettings.notificationsEnabled
    val biometric = AppSettings.biometricEnabled
    val idiomaIngles = AppSettings.useEnglish

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = stringResource(R.string.settings_title),
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.settings_section_app),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                SwitchRow(
                    title = stringResource(R.string.settings_dark_mode),
                    subtitle = stringResource(R.string.settings_dark_mode_hint),
                    checked = darkMode,
                    onCheckedChange = { AppSettings.darkMode = it }
                )
            }

            item {
                SwitchRow(
                    title = stringResource(R.string.settings_notifications),
                    subtitle = stringResource(R.string.settings_notifications_hint),
                    checked = notifications,
                    onCheckedChange = { AppSettings.notificationsEnabled = it }
                )
            }

            item {
                SwitchRow(
                    title = stringResource(R.string.settings_biometric),
                    subtitle = stringResource(R.string.settings_biometric_hint),
                    checked = biometric,
                    onCheckedChange = { AppSettings.biometricEnabled = it }
                )
            }

            item {
                Text(
                    text = stringResource(R.string.settings_section_language),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                SwitchRow(
                    title = stringResource(R.string.settings_lang_english),
                    subtitle = stringResource(R.string.settings_lang_hint),
                    checked = idiomaIngles,
                    onCheckedChange = { AppSettings.useEnglish = it }
                )
            }

            item {
                Text(
                    text = stringResource(R.string.settings_about),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.app_name), fontWeight = FontWeight.SemiBold)
                        Text(
                            text = stringResource(R.string.settings_version, "1.0.0"),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.settings_authors),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
