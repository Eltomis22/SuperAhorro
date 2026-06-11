package com.undef.superahorro.Loza.Urieta.data

import android.util.Log
import com.undef.superahorro.Loza.Urieta.data.local.CompraDao
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.data.model.CompraConProductos
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import com.undef.superahorro.Loza.Urieta.data.model.User
import com.undef.superahorro.Loza.Urieta.data.remote.SuperAhorroApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SuperAhorroRepository(
    private val compraDao: CompraDao,
    private val api: SuperAhorroApi // Inyección de la API de Retrofit
) {

    // --- SESIÓN ---
    suspend fun obtenerUsuarioActual(): User = withContext(Dispatchers.IO) {
        MockData.usuarioActual
    }

    // --- COMPRAS (Lectura) ---
    
    /** Obtiene todas las compras registradas en tiempo real */
    fun obtenerTodasLasComprasFlow(): Flow<List<Compra>> = compraDao.obtenerTodasLasCompras()

    /** Obtiene el detalle de una compra específica por su ID */
    suspend fun obtenerCompraPorId(id: Int): Compra? = withContext(Dispatchers.IO) {
        compraDao.obtenerCompraPorId(id)
    }

    /** Obtiene una compra con todos sus productos asociados */
    fun obtenerCompraConProductos(id: Int): Flow<CompraConProductos?> = 
        compraDao.obtenerCompraConProductos(id)

    // --- DROPDOWNS Y NETWORKING (GET) ---

    /**
     * Obtiene la lista de supermercados. 
     * Implementa el requisito GET: intenta descargar de la API, 
     * si falla usa los datos de MockData.
     */
    suspend fun obtenerSupermercados(): List<String> = withContext(Dispatchers.IO) {
        try {
            val listaRemota = api.obtenerSupermercados()
            Log.d("Repository", "Supermercados obtenidos de la API: ${listaRemota.size}")
            listaRemota
        } catch (e: Exception) {
            Log.e("Repository", "Error al llamar a la API: ${e.message}. Usando MockData.")
            MockData.supermercados // Fallback
        }
    }

    suspend fun obtenerGastoMensual(): List<Pair<String, Double>> = withContext(Dispatchers.IO) {
        MockData.gastoMensual
    }

    suspend fun obtenerGastoPorSupermercado(): List<Pair<String, Double>> =
        withContext(Dispatchers.IO) {
            MockData.gastoPorSupermercado
        }

    // --- OPERACIONES DE ESCRITURA ---

    suspend fun agregarCompra(compra: Compra): Long = withContext(Dispatchers.IO) {
        compraDao.insertarCompra(compra)
    }

    suspend fun actualizarCompra(compra: Compra) = withContext(Dispatchers.IO) {
        compraDao.actualizarCompra(compra)
    }

    suspend fun eliminarCompra(compraId: Int) = withContext(Dispatchers.IO) {
        compraDao.eliminarCompraPorId(compraId)
    }

    suspend fun agregarProducto(compraId: Int, producto: Producto) = withContext(Dispatchers.IO) {
        compraDao.insertarProducto(producto.copy(compraId = compraId))
    }

    suspend fun eliminarProducto(productoId: Int) = withContext(Dispatchers.IO) {
        compraDao.eliminarProductoPorId(productoId)
    }
}
