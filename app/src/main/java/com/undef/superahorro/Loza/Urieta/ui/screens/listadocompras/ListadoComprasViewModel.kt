package com.undef.superahorro.Loza.Urieta.ui.screens.listadocompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListadoComprasViewModel(
    private val repository: SuperAhorroRepository = SuperAhorroRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListadoComprasUiState())
    val uiState: StateFlow<ListadoComprasUiState> = _uiState.asStateFlow()

    init {
        cargarCompras()
    }

    fun cargarCompras() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val lista = repository.obtenerCompras()
                _uiState.update { it.copy(isLoading = false, compras = lista) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
