package com.undef.superahorro.Loza.Urieta.ui.screens.miperfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.undef.superahorro.Loza.Urieta.data.SettingsRepository
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MiPerfilViewModel(
    private val repository: SuperAhorroRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MiPerfilUiState())
    val uiState: StateFlow<MiPerfilUiState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
    }

    private fun cargarPerfil() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            combine(
                settingsRepository.userNameFlow,
                settingsRepository.userEmailFlow
            ) { name, email ->
                User(id = 0, nombre = name, email = email)
            }.collect { user ->
                _uiState.update { it.copy(isLoading = false, usuario = user) }
            }
        }
    }

    fun actualizarNombre(nuevoNombre: String) {
        viewModelScope.launch {
            val emailActual = settingsRepository.userEmailFlow.first()
            // Actualizamos en Room
            repository.actualizarNombreUsuario(emailActual, nuevoNombre)
            // Actualizamos en DataStore
            settingsRepository.setLoggedIn(isLoggedIn = true, name = nuevoNombre, email = emailActual)
        }
    }

    fun actualizarClave(nuevaClave: String) {
        viewModelScope.launch {
            val emailActual = settingsRepository.userEmailFlow.first()
            repository.actualizarClaveUsuario(emailActual, nuevaClave)
        }
    }

    fun actualizarEmail(nuevoEmail: String) {
        viewModelScope.launch {
            val emailViejo = settingsRepository.userEmailFlow.first()
            val nombreActual = settingsRepository.userNameFlow.first()
            // Actualizamos en Room
            repository.actualizarEmailUsuario(emailViejo, nuevoEmail)
            // Actualizamos en DataStore
            settingsRepository.setLoggedIn(isLoggedIn = true, name = nombreActual, email = nuevoEmail)
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            settingsRepository.clearSession()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as com.undef.superahorro.Loza.Urieta.SuperAhorroApp
                return MiPerfilViewModel(
                    application.repository,
                    SettingsRepository(application)
                ) as T
            }
        }
    }
}
