package com.undef.superahorro.Loza.Urieta.ui.screens.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistorialComprasUiState(
    val isLoading: Boolean = false,
    val comprasAgrupadas: Map<String, List<Compra>> = emptyMap(),
    val error: String? = null
)

class HistorialComprasViewModel(
    private val repository: SuperAhorroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialComprasUiState())
    val uiState: StateFlow<HistorialComprasUiState> = _uiState.asStateFlow()

    init {
        cargarCompras()
    }

    private fun cargarCompras() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                repository.obtenerTodasLasComprasFlow().collect { listaCompras ->
                    val comprasOrdenadas = listaCompras.sortedByDescending { it.fecha }
                    val agrupadas = comprasOrdenadas.groupBy { it.fecha.substring(0, 7) }
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            comprasAgrupadas = agrupadas 
                        ) 
                    }
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
                return HistorialComprasViewModel(application.repository) as T
            }
        }
    }
}
