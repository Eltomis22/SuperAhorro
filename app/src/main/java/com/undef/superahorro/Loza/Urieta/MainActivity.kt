package com.undef.superahorro.Loza.Urieta

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.undef.superahorro.Loza.Urieta.navigation.SuperAhorroNavGraph
import com.undef.superahorro.Loza.Urieta.ui.AppSettings
import com.undef.superahorro.Loza.Urieta.ui.theme.SuperAhorroTheme
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {

            // 1. THEME: Modo claro/oscuro reactivo
            SuperAhorroTheme(darkTheme = AppSettings.darkMode) {

                // 2. LOCALE: Cambio de idioma
                val targetLocale = if (AppSettings.useEnglish) Locale("en") else Locale("es")
                val baseConfig = LocalConfiguration.current
                val baseContext = LocalContext.current

                val newConfig = remember(targetLocale, baseConfig) {
                    Configuration(baseConfig).apply { setLocale(targetLocale) }
                }
                val newContext = remember(newConfig) {
                    baseContext.createConfigurationContext(newConfig)
                }

                CompositionLocalProvider(
                    LocalConfiguration provides newConfig,
                    LocalContext provides newContext,
                ) {
                    // Surface = contenedor base con el background del theme aplicado.
                    Surface(modifier = Modifier.fillMaxSize()) {
                        // Acá arranca toda la navegación de la app.
                        SuperAhorroNavGraph()
                    }
                }
            }
        }
    }
}