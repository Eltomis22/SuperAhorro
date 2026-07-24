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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

class SuperAhorroRepository(
    private val context: android.content.Context,
    private val compraDao: CompraDao,
    private val productoDao: ProductoDao,
    private val userDao: UserDao,
    private val api: SuperAhorroApi,
    private val settingsRepository: com.undef.superahorro.Loza.Urieta.data.SettingsRepository
) {

    // --- SESIÓN Y USUARIOS ---

    suspend fun registrarUsuario(nombre: String, email: String, clave: String) = withContext(Dispatchers.IO) {
        // 1. Local
        val entity = UserEntity(nombre = nombre, email = email, clave = clave)
        userDao.insertarUsuario(entity)
        
        // 2. Cloud
        try {
            api.registrarUsuarioCloud(com.undef.superahorro.Loza.Urieta.data.remote.AuthRequest(email, clave, nombre))
        } catch (e: Exception) {
            Log.e("Repository", "Error al registrar en la nube: ${e.message}")
        }
    }

    suspend fun validarCredenciales(email: String, clave: String): User? = withContext(Dispatchers.IO) {
        // 1. Intentamos Local primero para rapidez
        val entity = userDao.obtenerUsuarioPorEmail(email)
        if (entity != null && entity.clave == clave) {
            return@withContext User(id = entity.id, nombre = entity.nombre, email = entity.email)
        }
        
        // 2. Si no está local, probamos en la nube
        try {
            val response = api.loginUsuarioCloud(com.undef.superahorro.Loza.Urieta.data.remote.AuthRequest(email, clave))
            if (response.isSuccessful && response.body()?.success == true) {
                val cloudUser = response.body()!!.user!!
                // Guardamos local para la próxima
                userDao.insertarUsuario(UserEntity(nombre = cloudUser.nombre, email = cloudUser.email, clave = clave))
                return@withContext User(id = 0, nombre = cloudUser.nombre, email = cloudUser.email)
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error en login cloud: ${e.message}")
        }
        
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
        // Nota: Los productos se borran por cascada al borrar las compras
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
        api.obtenerSupermercados()
    }

    suspend fun obtenerGastoMensual(): List<Pair<String, Double>> = withContext(Dispatchers.IO) {
        val email = settingsRepository.userEmailFlow.first()
        val compras = compraDao.obtenerTodasLasComprasSnapshot(email)
        if (compras.isEmpty()) return@withContext emptyList<Pair<String, Double>>()
        
        compras.groupBy { it.fecha.take(7) }
            .map { (mes, lista) -> mes to lista.sumOf { it.total } }
            .sortedBy { it.first }
            .takeLast(6)
    }

    suspend fun obtenerGastoPorSupermercado(): List<Pair<String, Double>> = withContext(Dispatchers.IO) {
        val email = settingsRepository.userEmailFlow.first()
        val compras = compraDao.obtenerTodasLasComprasSnapshot(email)
        if (compras.isEmpty()) return@withContext emptyList<Pair<String, Double>>()

        compras.groupBy { it.supermercado }
            .map { (superName, lista) -> superName to lista.sumOf { it.total } }
            .sortedByDescending { it.second }
    }

    suspend fun obtenerProductosMasComprados(): List<Pair<String, Int>> = withContext(Dispatchers.IO) {
        val email = settingsRepository.userEmailFlow.first()
        val productos = productoDao.obtenerTodosLosProductosSnapshot(email)
        if (productos.isEmpty()) return@withContext emptyList<Pair<String, Int>>()

        productos.groupBy { it.nombre }
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
        val response = api.sincronizarCompra(compra)
        if (!response.isSuccessful) {
            throw Exception("Error de sincronización: ${response.code()} ${response.message()}")
        }
        Log.d("Repository", "Sincronización exitosa")
    }

    suspend fun sincronizarDesdeLaNube() = withContext(Dispatchers.IO) {
        try {
            val email = settingsRepository.userEmailFlow.first()
            val comprasRemotas = api.obtenerComprasRemotas(email)
            
            // Guardamos cada compra descargada en el Room local
            comprasRemotas.forEach { compraRemota ->
                compraDao.insertarCompra(compraRemota)
            }
            Log.d("Repository", "Sincronización de bajada completada: ${comprasRemotas.size} compras recuperadas")
        } catch (e: Exception) {
            Log.e("Repository", "Error en sincronización de bajada: ${e.message}")
        }
    }

    suspend fun eliminarCompra(compraId: Int) = withContext(Dispatchers.IO) {
        // 1. Obtener la compra para ver si tiene ticket
        val compra = compraDao.obtenerCompraPorId(compraId)
        
        // 2. Si tiene ticket, intentar borrar el archivo físico para liberar espacio
        compra?.ticketImagenUri?.let { uriString ->
            try {
                // Forma segura de obtener el archivo desde una URI de FileProvider
                // Solo si la URI es local (empieza con content:// o file://)
                val uri = android.net.Uri.parse(uriString)
                if (uri.scheme == "file") {
                    val file = File(uri.path ?: "")
                    if (file.exists()) file.delete()
                } else if (uri.scheme == "content") {
                    // Para URIs de tipo content (FileProvider), borrar el archivo real
                    // que reside en la carpeta Pictures de la app.
                    val fileName = uri.lastPathSegment // ej: my_images/ticket_123.jpg
                    fileName?.split("/")?.last()?.let { nameOnly ->
                        val file = File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), nameOnly)
                        if (file.exists()) {
                            file.delete()
                            Log.d("Repository", "Archivo de ticket eliminado: $nameOnly")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("Repository", "No se pudo borrar el archivo físico del ticket: ${e.message}")
            }
        }

        // 3. Borrar de la base de datos local (Room borrará productos por cascada)
        compraDao.eliminarCompraPorId(compraId)

        // 4. Avisar al servidor para sincronizar el borrado
        try {
            api.eliminarCompraRemota(compraId)
        } catch (e: Exception) {
            Log.e("Repository", "Error al eliminar en la nube: ${e.message}")
        }
    }

    suspend fun agregarProducto(compraId: Int, producto: Producto) = withContext(Dispatchers.IO) {
        productoDao.insertarProducto(producto.copy(compraId = compraId))
    }

    // --- CHAT CON IA (Vía Backend) ---

    suspend fun consultarIA(mensaje: String): String = withContext(Dispatchers.IO) {
        try {
            val response = api.enviarMensajeChat(com.undef.superahorro.Loza.Urieta.data.remote.ChatRequest(mensaje))
            response.response
        } catch (e: Exception) {
            "Error al consultar al servidor: ${e.message}"
        }
    }

    // --- ALGORITMO DEL BANQUERO ---

    suspend fun obtenerMisPresupuestos(): List<com.undef.superahorro.Loza.Urieta.data.remote.BudgetLimit> = withContext(Dispatchers.IO) {
        try {
            val email = settingsRepository.userEmailFlow.first()
            api.obtenerPresupuestos(email)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun guardarMisPresupuestos(lista: List<com.undef.superahorro.Loza.Urieta.data.remote.BudgetLimit>) = withContext(Dispatchers.IO) {
        try {
            val email = settingsRepository.userEmailFlow.first()
            api.guardarPresupuestos(com.undef.superahorro.Loza.Urieta.data.remote.SaveBudgetRequest(email, lista))
        } catch (e: Exception) {
            Log.e("Repository", "Error al guardar presupuestos: ${e.message}")
        }
    }

    suspend fun verificarPresupuesto(categoria: String, monto: Double): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val userEmail = settingsRepository.userEmailFlow.first()
            val response = api.verificarGastoSeguro(
                com.undef.superahorro.Loza.Urieta.data.remote.BudgetCheckRequest(
                    categoria = categoria, 
                    montoSolicitado = monto,
                    presupuestoTotal = null, // Podríamos permitir configurarlo en el futuro
                    usuarioEmail = userEmail
                )
            )
            Pair(response.safe, response.message)
        } catch (e: Exception) {
            // El texto ahora está en strings.xml, pero como el repositorio no tiene fácil acceso a context, 
            // lo ideal es que el ViewModel maneje el texto o pasarle un default y que la UI lo traduzca.
            // Para mantener consistencia con el resto del repo, dejamos el texto pero avisamos que es fallback.
            Pair(true, "No se pudo verificar el presupuesto (Modo Offline).")
        }
    }
}
