package com.undef.superahorro.Loza.Urieta.data.remote

import com.undef.superahorro.Loza.Urieta.data.model.Compra
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interfaz de Retrofit para definir las llamadas a la API remota.
 * Según el enunciado, necesitamos al menos un GET y un POST.
 */
interface SuperAhorroApi {

    /**
     * GET: Obtiene la lista de supermercados sugeridos desde el servidor.
     */
    @GET("api/v1/supermercados")
    suspend fun obtenerSupermercados(): List<String>

    /**
     * POST: Sincroniza una compra local con el servidor.
     */
    @POST("compras")
    suspend fun sincronizarCompra(@Body compra: Compra): Response<ApiResponse>

    /**
     * POST: Envía un mensaje al servicio de Chat con IA en el servidor.
     */
    @POST("api/v1/chat")
    suspend fun enviarMensajeChat(@Body request: ChatRequest): ChatResponse

    /**
     * POST: Verifica si un gasto es seguro basándose en el presupuesto mensual (Algoritmo del Banquero).
     */
    @POST("api/v1/compras/verificar")
    suspend fun verificarGastoSeguro(@Body request: VerificarGastoRequest): VerificarGastoResponse

    companion object {
        const val BASE_URL = "http://10.0.2.2:3000/"
    }
}

/**
 * Modelo de respuesta genérico para la API.
 */
data class ApiResponse(
    val success: Boolean,
    val message: String? = null
)

/**
 * Modelos para el Chat con IA (Delegado al backend)
 */
data class ChatRequest(
    val mensaje: String
)

data class ChatResponse(
    val respuesta: String
)

/**
 * Modelos para la simulación de Gasto Seguro (Algoritmo del Banquero)
 */
data class VerificarGastoRequest(
    val monto: Double
)

data class VerificarGastoResponse(
    val seguro: Boolean,
    val mensaje: String
)
