package com.undef.superahorro.Loza.Urieta.ui.screens.perfil

import com.undef.superahorro.data.model.User

data class MiPerfilUiState(
    val isLoading: Boolean = false,
    val usuario: User? = null,
    val error: String? = null
)