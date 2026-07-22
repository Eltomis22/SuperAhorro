package com.undef.superahorro.Loza.Urieta.ui.screens.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.model.ChatMessage
import com.undef.superahorro.Loza.Urieta.data.remote.ChatRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage("¡Hola! Soy tu asistente de SuperAhorro conectado a mi propio servidor. ¿En qué puedo ayudarte hoy?", false)
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val repository: SuperAhorroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMessage = ChatMessage(userText, true)
        _uiState.update { it.copy(
            messages = it.messages + userMessage,
            isLoading = true,
            error = null
        ) }

        viewModelScope.launch {
            try {
                // Ahora consultamos a la IA a través de nuestro propio Backend
                val aiText = repository.consultarIA(userText)
                
                _uiState.update { it.copy(
                    messages = it.messages + ChatMessage(response.respuesta, false),
                    isLoading = false
                ) }
            } catch (e: Exception) {
                val errorDetails = e.message ?: "Error desconocido"
                Log.e("ChatViewModel", "Error en Chat: $errorDetails")
                
                _uiState.update { it.copy(
                    messages = it.messages + ChatMessage("Error: No pude contactar con el asistente.", false),
                    isLoading = false,
                    error = errorDetails
                ) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as com.undef.superahorro.Loza.Urieta.SuperAhorroApp
                return ChatViewModel(application.repository) as T
            }
        }
    }
}
