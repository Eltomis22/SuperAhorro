package com.undef.superahorro.Loza.Urieta.ui.screens.listadocompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListadoComprasViewModel(
    private val repository: SuperAhorroRepository
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
                // Migramos de obtenerCompras() (MockData) a observar el Flow real de Room
                repository.obtenerTodasLasComprasFlow().collect { lista ->
                    _uiState.update { it.copy(isLoading = false, compras = lista) }
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
                return ListadoComprasViewModel(application.repository) as T
            }
        }
    }
}
