package com.undef.superahorro.Loza.Urieta.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage("¡Hola! Soy tu asistente de SuperAhorro. ¿En qué puedo ayudarte con tus compras hoy?", false)
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val repository: SuperAhorroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // CONFIGURACIÓN DE GEMINI
    // NOTA: En una app real, la API KEY no debe estar en el código.
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "TU_API_KEY_AQUI" // El usuario debe poner su clave aquí
    )

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
                // Obtenemos el contexto de las compras reales del usuario
                val contextData = repository.obtenerResumenParaIA()
                
                val prompt = content {
                    text("Eres un asistente experto en ahorro y finanzas personales para la app 'SuperAhorro'.")
                    text("Aquí están los datos de compras reales del usuario: $contextData")
                    text("Responde de forma breve y amigable a la siguiente consulta del usuario: $userText")
                }

                val response = generativeModel.generateContent(prompt)
                val aiText = response.text ?: "Lo siento, no pude procesar esa consulta."
                
                _uiState.update { it.copy(
                    messages = it.messages + ChatMessage(aiText, false),
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    messages = it.messages + ChatMessage("Error de conexión con la IA. Verifica tu API Key.", false),
                    isLoading = false,
                    error = e.message
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
