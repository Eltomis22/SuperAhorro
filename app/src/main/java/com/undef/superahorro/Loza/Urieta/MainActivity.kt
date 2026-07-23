package com.undef.superahorro.Loza.Urieta

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
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
            var isUnlocked by remember { mutableStateOf(false) }
            var biometricEnabled by remember { mutableStateOf(false) }

            // Cargamos preferencias iniciales y verificamos biometría
            LaunchedEffect(Unit) {
                biometricEnabled = settingsRepo.biometricEnabledFlow.first()
                AppSettings.darkMode = settingsRepo.darkModeFlow.first()
                
                if (biometricEnabled && BiometricHelper.isBiometricAvailable(this@MainActivity)) {
                    BiometricHelper.showBiometricPrompt(
                        activity = this@MainActivity,
                        onSuccess = { isUnlocked = true },
                        onError = { error ->
                            Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                            // En caso de error, podrías forzar el cierre o permitir login por clave
                        }
                    )
                } else {
                    isUnlocked = true
                }
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
