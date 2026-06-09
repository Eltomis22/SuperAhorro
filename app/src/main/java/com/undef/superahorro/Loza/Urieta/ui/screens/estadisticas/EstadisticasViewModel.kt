package com.undef.superahorro.Loza.Urieta.ui.screens.estadisticas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EstadisticasViewModel(
    private val repository: SuperAhorroRepository = SuperAhorroRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstadisticasUiState())
    val uiState: StateFlow<EstadisticasUiState> = _uiState.asStateFlow()

    init {
        cargarEstadisticas()
    }

    private fun cargarEstadisticas() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val mensual = repository.obtenerGastoMensual()
                val porSuper = repository.obtenerGastoPorSupermercado()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        gastoMensual = mensual,
                        gastoPorSupermercado = porSuper
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
