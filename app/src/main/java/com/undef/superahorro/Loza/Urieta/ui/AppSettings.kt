package com.undef.superahorro.Loza.Urieta.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Estado global de configuración de la app.
 */
object AppSettings {

    /** Si está activado, la app se renderiza en modo oscuro. */
    var darkMode by mutableStateOf(false)

    /** Si están activadas las notificaciones (placeholder). */
    var notificationsEnabled by mutableStateOf(true)

    /** Si está activada la autenticación biométrica (placeholder). */
    var biometricEnabled by mutableStateOf(false)
}
