package com.undef.superahorro.Loza.Urieta.data

import com.undef.superahorro.Loza.Urieta.data.local.CompraDao
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.data.model.CompraConProductos
import com.undef.superahorro.Loza.Urieta.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SuperAhorroRepository(private val compraDao: CompraDao) {

    // --- SESIÓN (Temporalmente sigue en MockData hasta el commit de DataStore/Session) ---
    suspend fun obtenerUsuarioActual(): User = withContext(Dispatchers.IO) {
        MockData.usuarioActual
    }

    // --- COMPRAS (Migrado a Room) ---
    
    /** Obtiene todas las compras registradas en tiempo real */
    fun obtenerTodasLasComprasFlow(): Flow<List<Compra>> = compraDao.obtenerTodasLasCompras()

    /** Obtiene todas las compras registradas (Snapshot) */
    suspend fun obtenerCompras(): List<Compra> = withContext(Dispatchers.IO) {
        // En una implementación real con Flow, solemos usar flow.first(), 
        // pero MockData devolvía List directamente.
        MockData.compras // Temporal hasta que migremos la escritura
    }

    /** Obtiene el detalle de una compra específica por su ID */
    suspend fun obtenerCompraPorId(id: Int): Compra? = withContext(Dispatchers.IO) {
        compraDao.obtenerCompraPorId(id)
    }

    /** Obtiene una compra con todos sus productos asociados */
    fun obtenerCompraConProductos(id: Int): Flow<CompraConProductos?> = 
        compraDao.obtenerCompraConProductos(id)

    // --- DROPDOWNS Y OTROS ---
    suspend fun obtenerSupermercados(): List<String> = withContext(Dispatchers.IO) {
        MockData.supermercados
    }

    suspend fun obtenerGastoMensual(): List<Pair<String, Double>> = withContext(Dispatchers.IO) {
        MockData.gastoMensual
    }

    suspend fun obtenerGastoPorSupermercado(): List<Pair<String, Double>> =
        withContext(Dispatchers.IO) {
            MockData.gastoPorSupermercado
        }

    // --- OPERACIONES DE ESCRITURA (MockData temporal hasta Commit 8) ---

    suspend fun agregarCompra(compra: Compra) = withContext(Dispatchers.IO) {
        MockData.agregarCompra(compra)
    }

    suspend fun actualizarCompra(compra: Compra) = withContext(Dispatchers.IO) {
        MockData.actualizarCompra(compra)
    }

    suspend fun eliminarCompra(compraId: Int) = withContext(Dispatchers.IO) {
        MockData.eliminarCompra(compraId)
    }

    suspend fun agregarProducto(compraId: Int, producto: com.undef.superahorro.Loza.Urieta.data.model.Producto) = withContext(Dispatchers.IO) {
        MockData.agregarProducto(compraId, producto)
    }

    suspend fun eliminarProducto(compraId: Int, productoId: Int) = withContext(Dispatchers.IO) {
        MockData.eliminarProducto(compraId, productoId)
    }

    suspend fun siguienteIdCompra(): Int = withContext(Dispatchers.IO) {
        MockData.siguienteIdCompra()
    }

    suspend fun siguienteIdProducto(): Int = withContext(Dispatchers.IO) {
        MockData.siguienteIdProducto()
    }
}
