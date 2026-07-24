package com.undef.superahorro.Loza.Urieta.ui.screens.detallecompra

import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.data.model.Producto

data class DetalleCompraUiState(
    val isLoading: Boolean = false,
    val compra: Compra? = null,
    val productos: List<Producto> = emptyList(),
    val error: String? = null
)
