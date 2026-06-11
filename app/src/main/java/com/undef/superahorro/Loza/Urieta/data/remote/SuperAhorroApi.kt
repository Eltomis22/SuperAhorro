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
    @GET("supermercados")
    suspend fun obtenerSupermercados(): List<String>

    /**
     * POST: Sincroniza una compra local con el servidor.
     */
    @POST("compras/sincronizar")
    suspend fun sincronizarCompra(@Body compra: Compra): Response<ApiResponse>

    companion object {
        const val BASE_URL = "https://api.superahorro.com/"
    }
}

/**
 * Modelo de respuesta genérico para la API.
 */
data class ApiResponse(
    val success: Boolean,
    val message: String? = null
)
