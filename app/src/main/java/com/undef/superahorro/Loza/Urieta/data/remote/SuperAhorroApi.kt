package com.undef.superahorro.Loza.Urieta.data.remote

import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.google.gson.annotations.SerializedName
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
     * POST: Envía un mensaje al chat con IA en el servidor.
     */
    @POST("chat")
    suspend fun enviarMensajeChat(@Body request: ChatRequest): ChatResponse

    /**
     * POST: Verifica si un gasto es seguro usando el Algoritmo del Banquero.
     */
    @POST("budget/check")
    suspend fun verificarGastoSeguro(@Body request: BudgetCheckRequest): BudgetCheckResponse

    companion object {
        // LOCAL: Usa esta URL para probar con el servidor corriendo en tu PC (Node.js)
        // const val BASE_URL = "http://10.0.2.2:3000/api/v1/"
        
        // PRODUCCIÓN: Cambia esto por la URL que te de Render después de desplegar
        const val BASE_URL = "https://tu-app-en-render.onrender.com/api/v1/"
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
 * Modelos para el Chat con IA
 */
data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val response: String
)

/**
 * Modelos para el Simulador de Gasto Seguro (Banquero)
 */
data class BudgetCheckRequest(
    val categoria: String,
    @SerializedName("monto_solicitado")
    val montoSolicitado: Double,
    @SerializedName("presupuesto_total")
    val presupuestoTotal: Double? = null
)

data class BudgetCheckResponse(
    val safe: Boolean,
    val message: String
)
