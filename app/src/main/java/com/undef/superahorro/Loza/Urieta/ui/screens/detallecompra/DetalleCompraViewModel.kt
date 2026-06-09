package com.undef.superahorro.Loza.Urieta.ui.screens.detallecompra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetalleCompraViewModel(
    private val repository: SuperAhorroRepository = SuperAhorroRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleCompraUiState())
    val uiState: StateFlow<DetalleCompraUiState> = _uiState.asStateFlow()

    fun cargarDetalleCompra(compraId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val detalle = repository.obtenerCompraPorId(compraId)
                _uiState.update { it.copy(isLoading = false, compra = detalle) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
