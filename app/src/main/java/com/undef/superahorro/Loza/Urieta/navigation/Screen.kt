package com.undef.superahorro.Loza.Urieta.navigation

/**
 * Rutas de navegación de la app, declaradas como sealed class.
 *
 * Cada object es un destino del NavHost. La sealed class garantiza que el
 * compilador conozca todas las rutas posibles y previene typos: si tipeás
 * mal un nombre de ruta, no compila.
 *
 * Las rutas con argumentos (DetalleCompra, NuevoProducto, EditarCompra)
 * exponen una función createRoute(...) que construye la URL final con el
 * id reemplazado, evitando armar strings a mano en el código.
 *
 * Ejemplo de uso:
 *   navController.navigate(Screen.DetalleCompra.createRoute(compraId = 5))
 *   // → navega a "detalle_compra/5"
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")

    object Home : Screen("home")
    object NuevaCompra : Screen("nueva_compra")
    object EditarCompra : Screen("editar_compra/{compraId}") {
        fun createRoute(compraId: Int) = "editar_compra/$compraId"
    }
    object NuevoProducto : Screen("nuevo_producto/{compraId}") {
        fun createRoute(compraId: Int) = "nuevo_producto/$compraId"
    }
    object ListadoCompras : Screen("listado_compras")
    object DetalleCompra : Screen("detalle_compra/{compraId}") {
        fun createRoute(compraId: Int) = "detalle_compra/$compraId"
    }
    object HistorialCompras : Screen("historial_compras")
    object Estadisticas : Screen("estadisticas")
    object MiPerfil : Screen("mi_perfil")
    object Settings : Screen("settings")
}
