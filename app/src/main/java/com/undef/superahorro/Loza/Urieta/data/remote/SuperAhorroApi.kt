package com.undef.superahorro.Loza.Urieta.data.remote

import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz de Retrofit para definir las llamadas a la API remota.
 * Según el enunciado, necesitamos al menos un GET y un POST.
 */
interface SuperAhorroApi {

    /**
     * GET: Obtiene la lista de supermercados sugeridos desde el servidor.
     */
    @GET("supermercados")
    suspend fun obtenerSupermercados(): List<String>

    /**
     * POST: Sincroniza una compra local con el servidor.
     */
    @POST("compras")
    suspend fun sincronizarCompra(@Body compra: Compra): Response<ApiResponse>

    /**
     * DELETE: Elimina una compra del servidor.
     */
    @DELETE("compras/{id}")
    suspend fun eliminarCompraRemota(@Path("id") idLocal: Int): Response<ApiResponse>

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

    /**
     * GET: Obtiene los presupuestos configurados por el usuario.
     */
    @GET("presupuestos")
    suspend fun obtenerPresupuestos(@Query("email") email: String): List<BudgetLimit>

    /**
     * POST: Guarda o actualiza los presupuestos del usuario.
     */
    @POST("presupuestos")
    suspend fun guardarPresupuestos(@Body request: SaveBudgetRequest): Response<ApiResponse>

    /**
     * POST: Registrar usuario en la nube.
     */
    @POST("usuarios/registrar")
    suspend fun registrarUsuarioCloud(@Body request: AuthRequest): Response<ApiResponse>

    /**
     * POST: Login de usuario en la nube.
     */
    @POST("usuarios/login")
    suspend fun loginUsuarioCloud(@Body request: AuthRequest): Response<AuthResponse>

    companion object {
        // LOCAL: Usa esta URL para probar con el servidor corriendo en tu PC (Node.js)
        // const val BASE_URL = "http://10.0.2.2:3000/api/v1/"
        
        // PRODUCCIÓN: Esta es tu URL real en Render
        const val BASE_URL = "https://super-ahorro-backend.onrender.com/api/v1/"
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
    val presupuestoTotal: Double? = null,
    @SerializedName("usuario_email")
    val usuarioEmail: String
)

data class BudgetCheckResponse(
    val safe: Boolean,
    val message: String
)

/**
 * Modelos para Autenticación Cloud
 */
data class AuthRequest(
    val email: String,
    val clave: String,
    val nombre: String? = null
)

data class AuthResponse(
    val success: Boolean,
    val message: String? = null,
    val user: User? = null
)

/** Usuario para AuthResponse */
data class User(
    val nombre: String,
    val email: String
)

/**
 * Modelos para Gestión de Presupuestos
 */
data class BudgetLimit(
    val categoria: String,
    @SerializedName("monto_maximo")
    val montoMaximo: Double
)

data class SaveBudgetRequest(
    val email: String,
    val presupuestos: List<BudgetLimit>
)
