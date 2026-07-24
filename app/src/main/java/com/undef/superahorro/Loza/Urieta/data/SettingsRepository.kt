package com.undef.superahorro.Loza.Urieta.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Extensión para inicializar el DataStore de forma global (Singleton)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val USE_ENGLISH = booleanPreferencesKey("use_english")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = booleanPreferencesKey("user_id") 
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val BIOMETRIC_USER_NAME = stringPreferencesKey("biometric_user_name")
        val BIOMETRIC_USER_EMAIL = stringPreferencesKey("biometric_user_email")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    /** Obtiene el nombre del usuario guardado */
    val userNameFlow: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.USER_NAME] ?: "Usuario" }

    /** Obtiene el email del usuario guardado */
    val userEmailFlow: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.USER_EMAIL] ?: "usuario@email.com" }

    /** Obtiene el nombre del usuario vinculado a la biometría */
    val biometricUserNameFlow: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.BIOMETRIC_USER_NAME] }

    /** Obtiene el email del usuario vinculado a la biometría */
    val biometricUserEmailFlow: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.BIOMETRIC_USER_EMAIL] }

    /** Observa si la biometría está activa */
    val biometricEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.BIOMETRIC_ENABLED] ?: false }

    /** Observa si las notificaciones están activas */
    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true }

    /** Observa si el usuario está logueado */
    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        }

    /** Observa el cambio del modo oscuro */
    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.DARK_MODE] ?: false
        }

    /** Observa el cambio de idioma */
    val useEnglishFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USE_ENGLISH] ?: false
        }

    /** Actualiza el modo oscuro en el disco */
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    /** Actualiza la biometría en el disco */
    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_ENABLED] = enabled
        }
    }

    /** Actualiza las notificaciones en el disco */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    /** Actualiza el idioma en el disco */
    suspend fun setUseEnglish(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_ENGLISH] = enabled
        }
    }

    /** Actualiza el estado de la sesión y datos básicos del usuario */
    suspend fun setLoggedIn(isLoggedIn: Boolean, name: String? = null, email: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
            name?.let { preferences[PreferencesKeys.USER_NAME] = it }
            email?.let { preferences[PreferencesKeys.USER_EMAIL] = it }
        }
    }

    /** Vincula un usuario específico a la biometría de este dispositivo */
    suspend fun setBiometricUser(name: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_USER_NAME] = name
            preferences[PreferencesKeys.BIOMETRIC_USER_EMAIL] = email
        }
    }

    /** Borra solo los datos de la sesión (útil para logout) */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.IS_LOGGED_IN)
            preferences.remove(PreferencesKeys.USER_NAME)
            preferences.remove(PreferencesKeys.USER_EMAIL)
            // NO borramos DARK_MODE ni BIOMETRIC_ENABLED ni BIOMETRIC_USER_* para que persistan
        }
    }
}
