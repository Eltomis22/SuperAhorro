package com.undef.superahorro.Loza.Urieta.ui.screens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.remote.BudgetLimit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PresupuestosUiState(
    val isLoading: Boolean = false,
    val presupuestos: List<BudgetLimit> = emptyList(),
    val error: String? = null,
    val guardadoExitoso: Boolean = false
)

class PresupuestosViewModel(
    private val repository: SuperAhorroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PresupuestosUiState())
    val uiState: StateFlow<PresupuestosUiState> = _uiState.asStateFlow()

    init {
        cargarPresupuestos()
    }

    private fun cargarPresupuestos() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val lista = repository.obtenerMisPresupuestos()
            if (lista.isEmpty()) {
                // Si no hay nada en la nube, inicializamos con valores base
                val iniciales = listOf(
                    BudgetLimit("Comida", 10000.0),
                    BudgetLimit("Servicios", 10000.0),
                    BudgetLimit("Ocio", 10000.0),
                    BudgetLimit("Otros", 10000.0)
                )
                _uiState.update { it.copy(isLoading = false, presupuestos = iniciales) }
            } else {
                _uiState.update { it.copy(isLoading = false, presupuestos = lista) }
            }
        }
    }

    fun actualizarLimite(categoria: String, nuevoMonto: Double) {
        val listaActualizada = _uiState.value.presupuestos.map {
            if (it.categoria == categoria) it.copy(montoMaximo = nuevoMonto) else it
        }
        _uiState.update { it.copy(presupuestos = listaActualizada, guardadoExitoso = false) }
    }

    fun guardar() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.guardarMisPresupuestos(_uiState.value.presupuestos)
            _uiState.update { it.copy(isLoading = false, guardadoExitoso = true) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as com.undef.superahorro.Loza.Urieta.SuperAhorroApp
                return PresupuestosViewModel(application.repository) as T
            }
        }
    }
}
