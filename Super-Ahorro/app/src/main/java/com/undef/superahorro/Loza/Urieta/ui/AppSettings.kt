package com.undef.superahorro.Loza.Urieta.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Estado global de configuración de la app.
 * En la 2da entrega esto se va a persistir con DataStore.
 *
 * Por ahora es un singleton en memoria — se reinicia cada vez que arranca la app.
 */
object AppSettings {

    /** Si está activado, la app se renderiza en modo oscuro. */
    var darkMode by mutableStateOf(false)

    /** Si está activado, la app fuerza locale "en"; si no, locale "es". */
    var useEnglish by mutableStateOf(false)

    /** Si están activadas las notificaciones (placeholder, todavía no usado). */
    var notificationsEnabled by mutableStateOf(true)

    /** Si está activada la autenticación biométrica (placeholder). */
    var biometricEnabled by mutableStateOf(false)
}
