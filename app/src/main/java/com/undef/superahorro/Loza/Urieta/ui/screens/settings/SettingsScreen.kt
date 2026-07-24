package com.undef.superahorro.Loza.Urieta.ui.screens.settings

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.ui.AppSettings
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar

/**
 * Pantalla de configuración de la app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {

    val darkMode by viewModel.darkModeFlow.collectAsStateWithLifecycle()
    val notifications by viewModel.notificationsEnabledFlow.collectAsStateWithLifecycle()
    val biometric by viewModel.biometricEnabledFlow.collectAsStateWithLifecycle()
    
    // Identidad del dueño actual de la huella
    val settingsRepo = com.undef.superahorro.Loza.Urieta.data.SettingsRepository(androidx.compose.ui.platform.LocalContext.current)
    val biometricOwner by settingsRepo.biometricUserEmailFlow.collectAsStateWithLifecycle(initialValue = null)

    val currentName by viewModel.userNameFlow.collectAsStateWithLifecycle()
    val currentEmail by viewModel.userEmailFlow.collectAsStateWithLifecycle()

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
                    onCheckedChange = { 
                        viewModel.setDarkMode(it)
                        AppSettings.darkMode = it
                    }
                )
            }

            item {
                SwitchRow(
                    title = stringResource(R.string.settings_notifications),
                    subtitle = stringResource(R.string.settings_notifications_hint),
                    checked = notifications,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                )
            }
            item {
                val context = androidx.compose.ui.platform.LocalContext.current
                var showReplacementDialog by remember { mutableStateOf(false) }

                if (showReplacementDialog) {
                    com.undef.superahorro.Loza.Urieta.ui.components.ConfirmDialog(
                        title = "Reemplazar huella",
                        message = "La biometría ya está vinculada a $biometricOwner. ¿Deseas vincularla a tu cuenta actual ($currentEmail)?",
                        confirmText = "Vincular",
                        onConfirm = {
                            showReplacementDialog = false
                            val activity = context as? androidx.fragment.app.FragmentActivity
                            if (activity != null) {
                                com.undef.superahorro.Loza.Urieta.ui.util.BiometricHelper.showBiometricPrompt(
                                    activity = activity,
                                    onSuccess = { 
                                        viewModel.setBiometricEnabled(true)
                                        viewModel.vincularUsuarioBiometria()
                                        android.widget.Toast.makeText(context, "Biometría vinculada a $currentEmail", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { error ->
                                        android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        },
                        onDismiss = { showReplacementDialog = false }
                    )
                }

                SwitchRow(
                    title = stringResource(R.string.settings_biometric),
                    subtitle = if (biometricOwner != null) 
                        "Vinculada a: $biometricOwner" 
                    else 
                        stringResource(R.string.settings_biometric_hint),
                    // El interruptor solo se ve ON si el usuario actual es el dueño
                    checked = biometric && (biometricOwner == currentEmail),
                    onCheckedChange = { isEnabled ->
                        if (isEnabled) {
                            if (biometricOwner != null && biometricOwner != currentEmail) {
                                showReplacementDialog = true
                            } else {
                                val activity = context as? androidx.fragment.app.FragmentActivity
                                if (activity != null && com.undef.superahorro.Loza.Urieta.ui.util.BiometricHelper.isBiometricAvailable(activity)) {
                                    com.undef.superahorro.Loza.Urieta.ui.util.BiometricHelper.showBiometricPrompt(
                                        activity = activity,
                                        onSuccess = { 
                                            viewModel.setBiometricEnabled(true)
                                            viewModel.vincularUsuarioBiometria()
                                            android.widget.Toast.makeText(context, "Biometría activada", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { error ->
                                            android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        } else {
                            viewModel.setBiometricEnabled(false)
                        }
                    }
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
