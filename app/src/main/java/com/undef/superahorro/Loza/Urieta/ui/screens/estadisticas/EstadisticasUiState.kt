package com.undef.superahorro.Loza.Urieta.ui.screens.estadisticas

data class EstadisticasUiState(
    val isLoading: Boolean = false,
    val gastoMensual: List<Pair<String, Double>> = emptyList(),
    val gastoPorSupermercado: List<Pair<String, Double>> = emptyList(),
    val error: String? = null
)