package com.undef.superahorro.Loza.Urieta.ui.screens.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NuevaCompraUiState(
    val isLoading: Boolean = false,
    val supermercados: List<String> = emptyList(),
    val compraCargada: Compra? = null,
    val guardadoExitoso: Int? = null,
    val error: String? = null
)

class NuevaCompraViewModel(
    private val repository: SuperAhorroRepository = SuperAhorroRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NuevaCompraUiState())
    val uiState: StateFlow<NuevaCompraUiState> = _uiState.asStateFlow()

    init {
        cargarSupermercados()
    }

    private fun cargarSupermercados() {
        viewModelScope.launch {
            val lista = repository.obtenerSupermercados()
            _uiState.update { it.copy(supermercados = lista) }
        }
    }

    fun cargarCompraParaEditar(id: Int) {
        viewModelScope.launch {
            val compra = repository.obtenerCompraPorId(id)
            _uiState.update { it.copy(compraCargada = compra) }
        }
    }

    fun guardarCompra(
        id: Int?,
        fecha: String,
        hora: String,
        supermercado: String,
        total: Double
    ) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val finalId = id ?: repository.siguienteIdCompra()
                val nuevaCompra = Compra(
                    id = finalId,
                    fecha = fecha,
                    hora = hora,
                    supermercado = supermercado,
                    total = total
                )

                if (id == null) {
                    repository.agregarCompra(nuevaCompra)
                } else {
                    repository.actualizarCompra(nuevaCompra)
                }
                
                _uiState.update { it.copy(isLoading = false, guardadoExitoso = finalId) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
