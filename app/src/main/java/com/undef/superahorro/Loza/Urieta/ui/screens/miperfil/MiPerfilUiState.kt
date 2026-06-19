package com.undef.superahorro.Loza.Urieta.ui.screens.miperfil

import com.undef.superahorro.Loza.Urieta.data.model.User

data class MiPerfilUiState(
    val isLoading: Boolean = false,
    val usuario: User? = null,
    val error: String? = null
)