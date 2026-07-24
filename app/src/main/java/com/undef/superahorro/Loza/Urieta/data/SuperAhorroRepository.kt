package com.undef.superahorro.Loza.Urieta.data

import android.util.Log
import com.undef.superahorro.Loza.Urieta.data.local.*
import com.undef.superahorro.Loza.Urieta.data.model.*
import com.undef.superahorro.Loza.Urieta.data.remote.*
import com.undef.superahorro.Loza.Urieta.ui.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

class SuperAhorroRepository(
    private val context: android.content.Context,
    private val compraDao: CompraDao,
    private val productoDao: ProductoDao,
    private val userDao: UserDao,
    private val supermercadoDao: SupermercadoDao,
    private val api: SuperAhorroApi,
    private val settingsRepository: SettingsRepository
) {

    // --- SESIÓN Y USUARIOS ---

    suspend fun registrarUsuario(nombre: String, email: String, clave: String) = withContext(Dispatchers.IO) {
        val entity = UserEntity(nombre = nombre, email = email, clave = clave)
        userDao.insertarUsuario(entity)
        try {
            api.registrarUsuarioCloud(AuthRequest(email, clave, nombre))
        } catch (_: Exception) {}
    }

    suspend fun validarCredenciales(email: String, clave: String): com.undef.superahorro.Loza.Urieta.data.model.User? = withContext(Dispatchers.IO) {
        val entity = userDao.obtenerUsuarioPorEmail(email)
        if (entity != null && entity.clave == clave) {
            return@withContext com.undef.superahorro.Loza.Urieta.data.model.User(id = entity.id, nombre = entity.nombre, email = entity.email)
        }
        try {
            val response = api.loginUsuarioCloud(AuthRequest(email, clave))
            if (response.isSuccessful && response.body()?.success == true) {
                val cloudUser = response.body()!!.user!!
                userDao.insertarUsuario(UserEntity(nombre = cloudUser.nombre, email = cloudUser.email, clave = clave))
                return@withContext com.undef.superahorro.Loza.Urieta.data.model.User(id = 0, nombre = cloudUser.nombre, email = cloudUser.email)
            }
        } catch (_: Exception) {}
        null
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

    suspend fun limpiarDatosLocales() = withContext(Dispatchers.IO) {
        compraDao.eliminarTodasLasCompras()
        userDao.eliminarTodosLosUsuarios()
    }

    // --- COMPRAS (Lectura) ---
    
    suspend fun obtenerTodasLasComprasFlow(): Flow<List<Compra>> {
        val email = settingsRepository.userEmailFlow.first()
        return compraDao.obtenerTodasLasCompras(email)
    }

    suspend fun obtenerCompraPorId(id: Int): Compra? = withContext(Dispatchers.IO) {
        compraDao.obtenerCompraPorId(id)
    }

    fun obtenerCompraConProductos(id: Int): Flow<CompraConProductos?> = 
        compraDao.obtenerCompraConProductos(id)

    // --- DROPDOWNS Y NETWORKING (GET) ---

    suspend fun obtenerSupermercados(): List<String> = withContext(Dispatchers.IO) {
        val cache = supermercadoDao.obtenerTodos()
        try {
            val remotos = api.obtenerSupermercados()
            if (remotos.isNotEmpty()) {
                supermercadoDao.limpiarCache()
                supermercadoDao.insertarVarios(remotos.map { SupermercadoEntity(it) })
                return@withContext remotos
            }
        } catch (_: Exception) {}
        cache.ifEmpty { listOf("Carrefour", "Coto", "Día%", "Vea") }
    }

    suspend fun obtenerGastoMensual(): List<Pair<String, Double>> = withContext(Dispatchers.IO) {
        val email = settingsRepository.userEmailFlow.first()
        val compras = compraDao.obtenerTodasLasComprasSnapshot(email)
        if (compras.isEmpty()) return@withContext emptyList()
        
        compras.groupBy { it.fecha?.take(7) ?: "N/A" }
            .map { (mes, lista) -> mes to lista.sumOf { it.total } }
            .sortedBy { it.first }
            .takeLast(6)
    }

    suspend fun obtenerGastoPorSupermercado(): List<Pair<String, Double>> = withContext(Dispatchers.IO) {
        val email = settingsRepository.userEmailFlow.first()
        val compras = compraDao.obtenerTodasLasComprasSnapshot(email)
        if (compras.isEmpty()) return@withContext emptyList()

        compras.groupBy { it.supermercado ?: "Otros" }
            .map { (superName, lista) -> superName to lista.sumOf { it.total } }
            .sortedByDescending { it.second }
    }

    suspend fun obtenerProductosMasComprados(): List<Pair<String, Int>> = withContext(Dispatchers.IO) {
        val email = settingsRepository.userEmailFlow.first()
        val productos = productoDao.obtenerTodosLosProductosSnapshot(email)
        if (productos.isEmpty()) return@withContext emptyList()

        productos.groupBy { it.nombre ?: "Sin nombre" }
            .map { (nombre, lista) -> nombre to lista.sumOf { it.cantidad } }
            .sortedByDescending { it.second }
            .take(5)
    }

    // --- OPERACIONES DE ESCRITURA Y NETWORKING (POST) ---

    suspend fun agregarCompra(compra: Compra): Long = withContext(Dispatchers.IO) {
        val userEmail = settingsRepository.userEmailFlow.first()
        val compraConUser = compra.copy(usuarioEmail = userEmail)
        val id = compraDao.insertarCompra(compraConUser)
        sincronizarConServidor(compraConUser.copy(id = id.toInt()))
        id
    }

    suspend fun actualizarCompra(compra: Compra) = withContext(Dispatchers.IO) {
        val userEmail = settingsRepository.userEmailFlow.first()
        val compraConUser = compra.copy(usuarioEmail = userEmail)
        compraDao.actualizarCompra(compraConUser)
        sincronizarConServidor(compraConUser)
    }

    private suspend fun sincronizarConServidor(compra: Compra) {
        try {
            val response = api.sincronizarCompra(compra)
            if (!response.isSuccessful) Log.e("Repository", "Error Sync: ${response.code()}")
        } catch (e: Exception) {
            Log.e("Repository", "Fallo red Sync: ${e.message}")
        }
    }

    suspend fun sincronizarDesdeLaNube() = withContext(Dispatchers.IO) {
        try {
            val email = settingsRepository.userEmailFlow.first()
            val comprasRemotas = api.obtenerComprasRemotas(email)
            comprasRemotas.forEach { compraRemota ->
                val compraSegura = compraRemota.copy(
                    fecha = compraRemota.fecha ?: "2026-01-01",
                    hora = (compraRemota.hora ?: "00:00").take(5),
                    supermercado = compraRemota.supermercado ?: "Desconocido",
                    categoria = compraRemota.categoria ?: "Otros",
                    usuarioEmail = compraRemota.usuarioEmail ?: email
                )
                val nuevoIdGenerado = compraDao.insertarCompra(compraSegura).toInt()
                compraRemota.productos.forEach { prod ->
                    productoDao.insertarProducto(prod.copy(compraId = nuevoIdGenerado))
                }
            }
        } catch (_: Exception) {}
    }

    suspend fun eliminarCompra(compraId: Int) = withContext(Dispatchers.IO) {
        val compra = compraDao.obtenerCompraPorId(compraId)
        compra?.ticketImagenUri?.let { uriString ->
            try {
                val uri = android.net.Uri.parse(uriString)
                if (uri.scheme == "file") {
                    val file = File(uri.path ?: "")
                    if (file.exists()) file.delete()
                } else if (uri.scheme == "content") {
                    val fileName = uri.lastPathSegment?.split("/")?.last()
                    val file = File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), fileName ?: "")
                    if (file.exists()) file.delete()
                }
            } catch (_: Exception) {}
        }
        compraDao.eliminarCompraPorId(compraId)
        try { api.eliminarCompraRemota(compraId) } catch (_: Exception) {}
    }

    suspend fun agregarProducto(compraId: Int, producto: Producto) = withContext(Dispatchers.IO) {
        productoDao.insertarProducto(producto.copy(compraId = compraId))
    }

    suspend fun consultarIA(mensaje: String): String = withContext(Dispatchers.IO) {
        try {
            api.enviarMensajeChat(ChatRequest(mensaje)).response
        } catch (e: Exception) {
            "Error al consultar al servidor: ${e.message}"
        }
    }

    suspend fun obtenerMisPresupuestos(): List<BudgetLimit> = withContext(Dispatchers.IO) {
        try {
            val email = settingsRepository.userEmailFlow.first()
            api.obtenerPresupuestos(email)
        } catch (_: Exception) { emptyList() }
    }

    suspend fun guardarMisPresupuestos(lista: List<BudgetLimit>) = withContext(Dispatchers.IO) {
        try {
            val email = settingsRepository.userEmailFlow.first()
            api.guardarPresupuestos(SaveBudgetRequest(email, lista))
        } catch (_: Exception) {}
    }

    suspend fun verificarPresupuesto(categoria: String, monto: Double): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val userEmail = settingsRepository.userEmailFlow.first()
            val response = api.verificarGastoSeguro(BudgetCheckRequest(categoria, monto, null, userEmail))
            if (!response.safe && settingsRepository.notificationsEnabledFlow.first()) {
                NotificationHelper.sendBudgetAlert(context, categoria, response.message)
            }
            Pair(response.safe, response.message)
        } catch (e: Exception) {
            Pair(true, "No se pudo verificar el presupuesto (Modo Offline).")
        }
    }

    suspend fun obtenerRankingPrecios(): Map<String, List<Pair<String, Double>>> = withContext(Dispatchers.IO) {
        val email = settingsRepository.userEmailFlow.first()
        val todosLosProductos = productoDao.obtenerTodosLosProductosSnapshot(email)
        val compras = compraDao.obtenerTodasLasComprasSnapshot(email)
        val mapCompras = compras.associate { it.id to (it.supermercado ?: "Desconocido") }

        todosLosProductos
            .filter { it.nombre != null }
            .groupBy { it.nombre!! }
            .mapValues { (_, prods) ->
                prods.map { p -> (mapCompras[p.compraId] ?: "Desconocido") to p.precio }
                .groupBy { it.first }
                .map { (name, list) -> name to list.minOf { it.second } }
                .sortedBy { it.second }
            }
    }
}
