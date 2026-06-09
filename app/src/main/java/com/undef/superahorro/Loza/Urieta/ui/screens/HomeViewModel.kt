package com.undef.superahorro.Loza.Urieta.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. EL ESTADO DE LA PANTALLA
data class HomeUiState(
    val isLoading: Boolean = false,
    val usuarioNombre: String = "",
    val ultimasCompras: List<Compra> = emptyList(),
    val totalMes: Double = 0.0,
    val error: String? = null
)

// 2. EL VIEWMODEL CON INYECCIÓN DE DEPENDENCIAS
class HomeViewModel(
    private val repository: SuperAhorroRepository // <-- Parámetro en el constructor
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        cargarDatosHome()
    }

    fun cargarDatosHome() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // Le pide los datos al Repositorio, no al MockData directamente
                val usuario = repository.obtenerUsuarioActual()
                val compras = repository.obtenerCompras()

                // Lógica de negocio orquestada por el ViewModel
                val ultimas = compras.take(3)
                val total = compras.filter { it.fecha.startsWith("2026") }.sumOf { it.total }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usuarioNombre = usuario.nombre,
                        ultimasCompras = ultimas,
                        totalMes = total
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}