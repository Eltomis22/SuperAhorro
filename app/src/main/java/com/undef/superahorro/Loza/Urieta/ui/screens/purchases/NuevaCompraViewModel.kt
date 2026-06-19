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

data class NuevaCompraUiState(
    val isLoading: Boolean = false,
    val supermercados: List<String> = emptyList(),
    val compraCargada: Compra? = null,
    val guardadoExitoso: Int? = null,
    val error: String? = null
)

class NuevaCompraViewModel(
    private val repository: SuperAhorroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NuevaCompraUiState())
    val uiState: StateFlow<NuevaCompraUiState> = _uiState.asStateFlow()

    init {
        cargarSupermercados()
    }

    private fun cargarSupermercados() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val lista = repository.obtenerSupermercados()
                _uiState.update { it.copy(isLoading = false, supermercados = lista) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Error al cargar supermercados: ${e.message}",
                        // Usamos datos locales como fallback si la API falla, pero informamos el error
                        supermercados = listOf("Coto", "Carrefour", "Jumbo", "ChangoMás", "Día")
                    )
                }
            }
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
        total: Double,
        ticketImagenUri: String? = null
    ) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val nuevaCompra = Compra(
                    id = id ?: 0, 
                    fecha = fecha,
                    hora = hora,
                    supermercado = supermercado,
                    total = total,
                    ticketImagenUri = ticketImagenUri
                )

                if (id == null) {
                    val newId = repository.agregarCompra(nuevaCompra)
                    _uiState.update { it.copy(isLoading = false, guardadoExitoso = newId.toInt()) }
                } else {
                    repository.actualizarCompra(nuevaCompra)
                    _uiState.update { it.copy(isLoading = false, guardadoExitoso = id) }
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
                return NuevaCompraViewModel(application.repository) as T
            }
        }
    }
}
