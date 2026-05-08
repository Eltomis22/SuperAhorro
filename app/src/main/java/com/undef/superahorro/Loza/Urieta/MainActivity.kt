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

/**
 * Activity principal y única de la aplicación.
 *
 * Cumple con varios requisitos de la consigna:
 * - Es la Activity declarada como LAUNCHER en el AndroidManifest.
 * - Toda la UI se monta acá vía setContent { ... } usando Jetpack Compose.
 * - Aplica internacionalización (cambio de idioma en runtime) y dark mode reactivo.
 *
 * NO maneja lógica de negocio: sólo orquesta el theme, el locale y delega
 * la navegación al SuperAhorroNavGraph.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // enableEdgeToEdge permite que el contenido se dibuje detrás de la status bar
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {

            // ----------------------------------------------------------------
            // 1. THEME: Modo claro/oscuro reactivo
            // ----------------------------------------------------------------
            // Lee de AppSettings.darkMode (singleton con mutableStateOf).
            // Cuando el usuario toca el switch en Settings, AppSettings cambia
            // y Compose recompone TODA la app con el theme nuevo, sin reiniciar.
            SuperAhorroTheme(darkTheme = AppSettings.darkMode) {

                // ------------------------------------------------------------
                // 2. LOCALE: Cambio de idioma en runtime (ES/EN)
                // ------------------------------------------------------------
                // Por defecto forzamos español. Si el usuario activa "Inglés"
                // en Settings, AppSettings.useEnglish pasa a true y reaplicamos
                // el Locale.
                val targetLocale = if (AppSettings.useEnglish) Locale("en") else Locale("es")
                val baseConfig = LocalConfiguration.current
                val baseContext = LocalContext.current

                // remember(key) recalcula sólo si cambia la key (evita rehacer
                // la Configuration en cada recomposición innecesariamente).
                val newConfig = remember(targetLocale, baseConfig) {
                    Configuration(baseConfig).apply { setLocale(targetLocale) }
                }
                val newContext = remember(newConfig) {
                    baseContext.createConfigurationContext(newConfig)
                }

                // CompositionLocalProvider "inyecta" la nueva Configuration y
                // el Context con locale forzado a TODA la jerarquía hija.
                // Esto hace que stringResource() lea del strings.xml correcto
                // (values/ vs values-en/) en cada pantalla.
                CompositionLocalProvider(
                    LocalConfiguration provides newConfig,
                    LocalContext provides newContext
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
