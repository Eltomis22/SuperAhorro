package com.undef.superahorro.Loza.Urieta.ui.screens.comparativa

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

data class ComparativaUiState(
    val isLoading: Boolean = false,
    val ranking: Map<String, List<Pair<String, Double>>> = emptyMap(),
    val error: String? = null
)

class ComparativaViewModel(
    private val repository: SuperAhorroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComparativaUiState())
    val uiState: StateFlow<ComparativaUiState> = _uiState.asStateFlow()

    init {
        cargarRanking()
    }

    fun cargarRanking() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val data = repository.obtenerRankingPrecios()
                _uiState.update { it.copy(isLoading = false, ranking = data) }
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
                return ComparativaViewModel(application.repository) as T
            }
        }
    }
}
