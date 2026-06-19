package com.undef.superahorro.Loza.Urieta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.undef.superahorro.Loza.Urieta.navigation.SuperAhorroNavGraph
import com.undef.superahorro.Loza.Urieta.ui.AppSettings
import com.undef.superahorro.Loza.Urieta.ui.theme.SuperAhorroTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            // THEME: Modo claro/oscuro reactivo
            // El idioma se maneja automáticamente por el sistema Android 
            // al no forzar un Locale específico aquí.
            SuperAhorroTheme(darkTheme = AppSettings.darkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SuperAhorroNavGraph()
                }
            }
        }
    }
}
