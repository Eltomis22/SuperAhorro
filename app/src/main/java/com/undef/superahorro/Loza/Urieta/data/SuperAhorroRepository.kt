package com.undef.superahorro.Loza.Urieta.data

import com.undef.superahorro.Loza.Urieta.data.local.CompraDao
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.data.model.CompraConProductos
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import com.undef.superahorro.Loza.Urieta.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SuperAhorroRepository(private val compraDao: CompraDao) {

    // --- SESIÓN (Temporalmente sigue en MockData hasta el commit de DataStore/Session) ---
    suspend fun obtenerUsuarioActual(): User = withContext(Dispatchers.IO) {
        MockData.usuarioActual
    }

    // --- COMPRAS (Lectura) ---
    
    /** Obtiene todas las compras registradas en tiempo real */
    fun obtenerTodasLasComprasFlow(): Flow<List<Compra>> = compraDao.obtenerTodasLasCompras()

    /** Obtiene todas las compras registradas (Snapshot) */
    suspend fun obtenerCompras(): List<Compra> = withContext(Dispatchers.IO) {
        // En una implementación real con Flow, solemos usar flow.first(), 
        // pero para compatibilidad devolvemos una lista vacía si no hay nada
        // o MockData si se prefiere para pruebas.
        emptyList() 
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

    // --- OPERACIONES DE ESCRITURA (MIGRADO A ROOM) ---

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
