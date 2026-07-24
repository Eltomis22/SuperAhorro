package com.undef.superahorro.Loza.Urieta.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.undef.superahorro.Loza.Urieta.data.SettingsRepository
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginExitoso: Boolean = false
)

class LoginViewModel(
    private val repository: SuperAhorroRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun iniciarSesion(email: String, clave: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                // Validación real contra Room
                val usuario = repository.validarCredenciales(email, clave)
                
                if (usuario != null) {
                    settingsRepository.setLoggedIn(
                        isLoggedIn = true,
                        name = usuario.nombre,
                        email = usuario.email
                    )
                    
                    // --- SINCRONIZACIÓN DE BAJADA ---
                    // Descargamos las compras de la nube para que el celular esté al día
                    repository.sincronizarDesdeLaNube()
                    
                    _uiState.update { it.copy(isLoading = false, loginExitoso = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Usuario o contraseña incorrectos") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun iniciarSesionBiometrica() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val email = settingsRepository.biometricUserEmailFlow.first()
                val nombre = settingsRepository.biometricUserNameFlow.first()
                
                android.util.Log.d("LoginVM", "Intento login biométrico para: $nombre ($email)")

                if (email != null && nombre != null) {
                    settingsRepository.setLoggedIn(
                        isLoggedIn = true,
                        name = nombre,
                        email = email
                    )
                    
                    // Sincronizamos para recuperar los datos reales de ese usuario
                    repository.sincronizarDesdeLaNube()
                    
                    _uiState.update { it.copy(isLoading = false, loginExitoso = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "No hay usuario vinculado a la biometría") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as com.undef.superahorro.Loza.Urieta.SuperAhorroApp
                return LoginViewModel(
                    application.repository,
                    SettingsRepository(application)
                ) as T
            }
        }
    }
}
