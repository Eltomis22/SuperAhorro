package com.undef.superahorro.Loza.Urieta.navigation

/**
 * Rutas de navegación de la app.
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
    object ChatIA : Screen("chat_ia")
    object Estadisticas : Screen("estadisticas")
    object MiPerfil : Screen("mi_perfil")
    object Settings : Screen("settings")
    object Presupuestos : Screen("presupuestos")
    object Comparativa : Screen("comparativa")
    
    // Perfil
    object EditarPerfil : Screen("editar_perfil")
    object CambiarClave : Screen("cambiar_clave")
    object CambiarEmail : Screen("cambiar_email")
}
