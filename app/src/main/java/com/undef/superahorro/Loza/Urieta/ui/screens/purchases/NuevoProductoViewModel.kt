package com.undef.superahorro.Loza.Urieta.ui.screens.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NuevoProductoUiState(
    val isLoading: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val productoCargado: Producto? = null,
    val error: String? = null
)

class NuevoProductoViewModel(
    private val repository: SuperAhorroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NuevoProductoUiState())
    val uiState: StateFlow<NuevoProductoUiState> = _uiState.asStateFlow()

    fun cargarProductoParaEditar(productoId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val producto = repository.obtenerProductoPorId(productoId)
                _uiState.update { it.copy(isLoading = false, productoCargado = producto) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun guardarProducto(
        id: Int = 0, // Si es > 0, es edición
        compraId: Int,
        codigo: String,
        nombre: String,
        descripcion: String,
        cantidad: Int,
        precio: Double
    ) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val producto = Producto(
                    id = id,
                    compraId = compraId,
                    codigo = codigo,
                    nombre = nombre,
                    descripcion = descripcion,
                    cantidad = cantidad,
                    precio = precio
                )
                repository.agregarProducto(compraId, producto)
                _uiState.update { it.copy(isLoading = false, guardadoExitoso = true) }
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
                return NuevoProductoViewModel(application.repository) as T
            }
        }
    }
}
