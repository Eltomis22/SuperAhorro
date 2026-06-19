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
    }

    /** Obtiene el nombre del usuario guardado */
    val userNameFlow: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.USER_NAME] ?: "Usuario" }

    /** Obtiene el email del usuario guardado */
    val userEmailFlow: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.USER_EMAIL] ?: "usuario@email.com" }

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

    /** Borra todos los datos (útil para logout) */
    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
