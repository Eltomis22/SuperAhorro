package com.undef.superahorro.Loza.Urieta.ui.screens.detallecompra

import com.undef.superahorro.Loza.Urieta.data.model.Compra

data class DetalleCompraUiState(
    val isLoading: Boolean = false,
    val compra: Compra? = null,
    val error: String? = null
)