package com.undef.superahorro.Loza.Urieta

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import com.undef.superahorro.Loza.Urieta.data.SettingsRepository
import com.undef.superahorro.Loza.Urieta.navigation.SuperAhorroNavGraph
import com.undef.superahorro.Loza.Urieta.ui.AppSettings
import com.undef.superahorro.Loza.Urieta.ui.theme.SuperAhorroTheme
import com.undef.superahorro.Loza.Urieta.ui.util.BiometricHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val settingsRepo = SettingsRepository(this)

        setContent {
            // Usamos rememberSaveable para que el estado de desbloqueo sobreviva si 
            // el sistema mata la app mientras usamos la cámara.
            var isUnlocked by rememberSaveable { mutableStateOf(false) }
            var biometricEnabled by remember { mutableStateOf(false) }

            // Cargamos preferencias iniciales
            LaunchedEffect(Unit) {
                biometricEnabled = settingsRepo.biometricEnabledFlow.first()
                AppSettings.darkMode = settingsRepo.darkModeFlow.first()
                
                // Entramos directo a la navegación. 
                // El login biométrico ahora es una opción explícita en la pantalla de Login.
                isUnlocked = true
            }

            SuperAhorroTheme(darkTheme = AppSettings.darkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (isUnlocked) {
                        SuperAhorroNavGraph()
                    }
                }
            }
        }
    }
}
