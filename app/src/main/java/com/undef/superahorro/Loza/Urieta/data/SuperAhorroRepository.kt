package com.undef.superahorro.Loza.Urieta.data

import android.util.Log
import com.undef.superahorro.Loza.Urieta.data.local.CompraDao
import com.undef.superahorro.Loza.Urieta.data.local.ProductoDao
import com.undef.superahorro.Loza.Urieta.data.local.UserDao
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.data.model.CompraConProductos
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import com.undef.superahorro.Loza.Urieta.data.model.User
import com.undef.superahorro.Loza.Urieta.data.model.UserEntity
import com.undef.superahorro.Loza.Urieta.data.remote.SuperAhorroApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SuperAhorroRepository(
    private val compraDao: CompraDao,
    private val productoDao: ProductoDao,
    private val userDao: UserDao,
    private val api: SuperAhorroApi
) {

    // --- SESIÓN Y USUARIOS ---

    suspend fun registrarUsuario(nombre: String, email: String, clave: String) = withContext(Dispatchers.IO) {
        val entity = UserEntity(nombre = nombre, email = email, clave = clave)
        userDao.insertarUsuario(entity)
    }

    suspend fun validarCredenciales(email: String, clave: String): User? = withContext(Dispatchers.IO) {
        val entity = userDao.obtenerUsuarioPorEmail(email)
        if (entity != null && entity.clave == clave) {
            User(id = entity.id, nombre = entity.nombre, email = entity.email)
        } else {
            null
        }
    }

    suspend fun actualizarNombreUsuario(email: String, nuevoNombre: String) = withContext(Dispatchers.IO) {
        userDao.actualizarNombre(email, nuevoNombre)
    }

    suspend fun actualizarClaveUsuario(email: String, nuevaClave: String) = withContext(Dispatchers.IO) {
        userDao.actualizarClave(email, nuevaClave)
    }

    suspend fun actualizarEmailUsuario(viejoEmail: String, nuevoEmail: String) = withContext(Dispatchers.IO) {
        userDao.actualizarEmail(viejoEmail, nuevoEmail)
    }

    // --- COMPRAS (Lectura) ---
    
    fun obtenerTodasLasComprasFlow(): Flow<List<Compra>> = compraDao.obtenerTodasLasCompras()

    suspend fun obtenerCompraPorId(id: Int): Compra? = withContext(Dispatchers.IO) {
        compraDao.obtenerCompraPorId(id)
    }

    fun obtenerCompraConProductos(id: Int): Flow<CompraConProductos?> = 
        compraDao.obtenerCompraConProductos(id)

    // --- DROPDOWNS Y NETWORKING (GET) ---

    suspend fun obtenerSupermercados(): List<String> = withContext(Dispatchers.IO) {
        api.obtenerSupermercados()
    }

    suspend fun obtenerGastoMensual(): List<Pair<String, Double>> = withContext(Dispatchers.IO) {
        val compras = compraDao.obtenerTodasLasComprasSnapshot()
        if (compras.isEmpty()) return@withContext emptyList<Pair<String, Double>>()
        
        compras.groupBy { it.fecha.take(7) }
            .map { (mes, lista) -> mes to lista.sumOf { it.total } }
            .sortedBy { it.first }
            .takeLast(6)
    }

    suspend fun obtenerGastoPorSupermercado(): List<Pair<String, Double>> = withContext(Dispatchers.IO) {
        val compras = compraDao.obtenerTodasLasComprasSnapshot()
        if (compras.isEmpty()) return@withContext emptyList<Pair<String, Double>>()

        compras.groupBy { it.supermercado }
            .map { (superName, lista) -> superName to lista.sumOf { it.total } }
            .sortedByDescending { it.second }
    }

    suspend fun obtenerProductosMasComprados(): List<Pair<String, Int>> = withContext(Dispatchers.IO) {
        val productos = productoDao.obtenerTodosLosProductosSnapshot()
        if (productos.isEmpty()) return@withContext emptyList<Pair<String, Int>>()

        productos.groupBy { it.nombre }
            .map { (nombre, lista) -> nombre to lista.sumOf { it.cantidad } }
            .sortedByDescending { it.second }
            .take(5)
    }

    // --- OPERACIONES DE ESCRITURA Y NETWORKING (POST) ---

    suspend fun agregarCompra(compra: Compra): Long = withContext(Dispatchers.IO) {
        val id = compraDao.insertarCompra(compra)
        sincronizarConServidor(compra.copy(id = id.toInt()))
        id
    }

    suspend fun actualizarCompra(compra: Compra) = withContext(Dispatchers.IO) {
        compraDao.actualizarCompra(compra)
        sincronizarConServidor(compra)
    }

    private suspend fun sincronizarConServidor(compra: Compra) {
        val response = api.sincronizarCompra(compra)
        if (!response.isSuccessful) {
            throw Exception("Error de sincronización: ${response.code()} ${response.message()}")
        }
        Log.d("Repository", "Sincronización exitosa")
    }

    suspend fun eliminarCompra(compraId: Int) = withContext(Dispatchers.IO) {
        compraDao.eliminarCompraPorId(compraId)
    }

    suspend fun agregarProducto(compraId: Int, producto: Producto) = withContext(Dispatchers.IO) {
        productoDao.insertarProducto(producto.copy(compraId = compraId))
    }

    suspend fun eliminarProducto(productoId: Int) = withContext(Dispatchers.IO) {
        productoDao.eliminarProductoPorId(productoId)
    }
}
