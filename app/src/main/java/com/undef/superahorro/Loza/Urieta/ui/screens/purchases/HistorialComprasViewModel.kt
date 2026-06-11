package com.undef.superahorro.Loza.Urieta.ui.screens.purchases

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import kotlinx.coroutines.launch

class HistorialComprasViewModel(
    private val repository: SuperAhorroRepository
) : ViewModel() {

    private val _comprasAgrupadas = mutableStateOf<Map<String, List<Compra>>>(emptyMap())
    val comprasAgrupadas: State<Map<String, List<Compra>>> = _comprasAgrupadas

    init {
        cargarCompras()
    }

    private fun cargarCompras() {
        viewModelScope.launch {
            try {
                repository.obtenerTodasLasComprasFlow().collect { listaCompras ->
                    val comprasOrdenadas = listaCompras.sortedByDescending { it.fecha }
                    _comprasAgrupadas.value = comprasOrdenadas.groupBy { it.fecha.substring(0, 7) }
                }
            } catch (e: Exception) {
                // Manejo de error
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
