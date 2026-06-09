package com.undef.superahorro.Loza.Urieta.ui.screens.miperfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MiPerfilViewModel(
    private val repository: SuperAhorroRepository = SuperAhorroRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MiPerfilUiState())
    val uiState: StateFlow<MiPerfilUiState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
    }

    private fun cargarPerfil() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val user = repository.obtenerUsuarioActual()
                _uiState.update { it.copy(isLoading = false, usuario = user) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}