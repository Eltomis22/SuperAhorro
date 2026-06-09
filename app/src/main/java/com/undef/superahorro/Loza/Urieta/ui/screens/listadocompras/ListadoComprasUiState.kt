package com.undef.superahorro.Loza.Urieta.ui.screens.listadocompras

import com.undef.superahorro.Loza.Urieta.data.model.Compra

data class ListadoComprasUiState(
    val isLoading: Boolean = false,
    val compras: List<Compra> = emptyList(),
    val error: String? = null
)