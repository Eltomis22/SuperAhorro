package com.undef.superahorro.Loza.Urieta.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.undef.superahorro.Loza.Urieta.data.SettingsRepository
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.undef.superahorro.Loza.Urieta.ui.util.Formatters

data class HomeUiState(
    val isLoading: Boolean = false,
    val usuarioNombre: String = "",
    val ultimasCompras: List<Compra> = emptyList(),
    val totalMes: Double = 0.0,
    val ahorroEstimado: Double = 0.0,
    val superMasVisitado: String = "N/A",
    val budgetMessage: String = "",
    val error: String? = null
)

class HomeViewModel(
    private val repository: SuperAhorroRepository,
    private val settingsRepository: SettingsRepository
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
                val nombre = settingsRepository.userNameFlow.first()
                val currentYearMonth = LocalDate.now().toString().take(7) // yyyy-MM
                
                repository.obtenerTodasLasComprasFlow().collect { compras ->
                    val ultimas = compras.take(3)
                    val comprasEsteMes = compras.filter { it.fecha?.startsWith(currentYearMonth) == true }
                    val totalSpent = comprasEsteMes.sumOf { it.total }
                    
                    // Lógica extra para "llenar" el inicio
                    val superMasFrecuente = compras.groupBy { it.supermercado }
                        .maxByOrNull { it.value.size }?.key ?: "N/A"
                    
                    // CÁLCULO REAL DE AHORRO Y PRESUPUESTO
                    val misPresupuestos = repository.obtenerMisPresupuestos()
                    val limiteTotal = if (misPresupuestos.isEmpty()) 40000.0 else misPresupuestos.sumOf { it.montoMaximo }
                    val ahorroCalculado = (limiteTotal - totalSpent).coerceAtLeast(0.0)
                    
                    val msg = if (totalSpent < limiteTotal) {
                        "Te quedan ${Formatters.formatearMoneda(limiteTotal - totalSpent)} de presupuesto"
                    } else {
                        "¡Has superado tu presupuesto mensual!"
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            usuarioNombre = nombre,
                            ultimasCompras = ultimas,
                            totalMes = totalSpent,
                            ahorroEstimado = ahorroCalculado,
                            superMasVisitado = superMasFrecuente,
                            budgetMessage = msg
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
                return HomeViewModel(
                    application.repository,
                    SettingsRepository(application)
                ) as T
            }
        }
    }
}
