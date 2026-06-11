package com.undef.superahorro.Loza.Urieta.ui.screens.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val error: String? = null
)

class NuevoProductoViewModel(
    private val repository: SuperAhorroRepository = SuperAhorroRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NuevoProductoUiState())
    val uiState: StateFlow<NuevoProductoUiState> = _uiState.asStateFlow()

    fun guardarProducto(
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
                val nuevoProducto = Producto(
                    id = repository.siguienteIdProducto(),
                    compraId = compraId,
                    codigo = codigo,
                    nombre = nombre,
                    descripcion = descripcion,
                    cantidad = cantidad,
                    precio = precio
                )
                repository.agregarProducto(compraId, nuevoProducto)
                _uiState.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
