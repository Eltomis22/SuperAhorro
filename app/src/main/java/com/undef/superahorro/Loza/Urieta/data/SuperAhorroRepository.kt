package com.undef.superahorro.Loza.Urieta.data

import com.undef.superahorro.Loza.Urieta.data.MockData
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SuperAhorroRepository {

    // Simula buscar el usuario (con un retraso opcional para simular base de datos/red en el futuro)
    suspend fun obtenerUsuarioActual(): User = withContext(Dispatchers.IO) {
        MockData.usuarioActual
    }

    // Obtiene todas las compras registradas
    suspend fun obtenerCompras(): List<Compra> = withContext(Dispatchers.IO) {
        MockData.compras
    }

    // Obtiene el detalle de una compra específica por su ID
    suspend fun obtenerCompraPorId(id: Int): Compra? = withContext(Dispatchers.IO) {
        MockData.compras.find { it.id == id }
    }

    // Obtiene la lista de supermercados para los desplegables (Dropdowns)
    suspend fun obtenerSupermercados(): List<String> = withContext(Dispatchers.IO) {
        MockData.supermercados
    }

    // Obtiene las estadísticas para la pantalla de Gráficos / Estadísticas
    suspend fun obtenerGastoMensual(): List<Pair<String, Double>> = withContext(Dispatchers.IO) {
        MockData.gastoMensual
    }

    suspend fun obtenerGastoPorSupermercado(): List<Pair<String, Double>> =
        withContext(Dispatchers.IO) {
            MockData.gastoPorSupermercado
        }

    // --- OPERACIONES DE ESCRITURA ---

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
