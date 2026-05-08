package com.undef.superahorro.Loza.Urieta.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.undef.superahorro.Loza.Urieta.ui.screens.DetalleCompraScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.EstadisticasScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.ForgotPasswordScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.HistorialComprasScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.HomeScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.ListadoComprasScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.LoginScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.MiPerfilScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.NuevaCompraScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.NuevoProductoScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.RegisterScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.SettingsScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.SplashScreen

/**
 * Grafo de navegación principal de la aplicación.
 *
 * Acá se declaran todas las rutas de navegación y a qué pantalla corresponde cada una.
 * Usamos Navigation Compose (uno de los temas centrales de la materia).
 *
 * Estructura general de cada destino:
 *   composable(ruta) { PantallaXXX(callbacks de navegación) }
 *
 * Los callbacks (onBack, onLoginSuccess, etc.) son lambdas que las pantallas
 * invocan cuando necesitan navegar a otro lado. Esto desacopla las pantallas
 * del NavController: las pantallas no saben hacia dónde van, sólo notifican
 * eventos. Eso facilita testearlas y reutilizarlas.
 */
@Composable
fun SuperAhorroNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        // La primera pantalla que se muestra al abrir la app.
        startDestination = Screen.Splash.route
    ) {

        // ================================================================
        // FLUJO DE AUTENTICACIÓN
        // ================================================================

        // Splash → tras 2 segundos pasa a Login (y queda fuera del back stack
        // para que no se pueda volver al splash con el botón atrás).
        composable(Screen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Login → si el usuario completa los campos válidos, va a Home.
        // Si toca "Registrate" o "¿Olvidaste tu contraseña?" navega a esas pantallas.
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        // popUpTo inclusive elimina Login del back stack para
                        // que el usuario no pueda volver atrás al login una vez logueado.
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Screen.Register.route) },
                onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ================================================================
        // PANTALLAS PRINCIPALES (con BottomBar)
        // ================================================================

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.ListadoCompras.route) {
            ListadoComprasScreen(navController = navController)
        }

        composable(Screen.HistorialCompras.route) {
            HistorialComprasScreen(navController = navController)
        }

        composable(Screen.Estadisticas.route) {
            EstadisticasScreen(navController = navController)
        }

        composable(Screen.MiPerfil.route) {
            MiPerfilScreen(
                navController = navController,
                onLogout = {
                    // popUpTo(0) inclusive limpia TODA la pila para que tras
                    // logout no se pueda volver con back a la pantalla anterior.
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        // ================================================================
        // FLUJO DE COMPRAS (CRUD)
        // ================================================================

        // Crear nueva compra → al guardar, navegamos directo a NuevoProducto
        // pasando el id de la compra recién creada.
        composable(Screen.NuevaCompra.route) {
            NuevaCompraScreen(
                onBack = { navController.popBackStack() },
                onCompraGuardada = { compraId ->
                    navController.navigate(Screen.NuevoProducto.createRoute(compraId)) {
                        // Eliminamos NuevaCompra del back stack: ya está guardada.
                        popUpTo(Screen.NuevaCompra.route) { inclusive = true }
                    }
                }
            )
        }

        // Editar compra existente → reutilizamos NuevaCompraScreen pasándole
        // el id como parámetro opcional. La pantalla se da cuenta sola que
        // está en modo edición (cambia el título y el botón).
        // navArgument define cómo se castea el {compraId} de la URL a Int.
        composable(
            route = Screen.EditarCompra.route,
            arguments = listOf(navArgument("compraId") { type = NavType.IntType })
        ) { backStackEntry ->
            val compraId = backStackEntry.arguments?.getInt("compraId") ?: 0
            NuevaCompraScreen(
                onBack = { navController.popBackStack() },
                onCompraGuardada = { navController.popBackStack() },
                compraIdParaEditar = compraId
            )
        }

        // Agregar un producto a una compra existente.
        // Recibe el compraId por argumento desde el caller (Detalle o NuevaCompra).
        composable(
            route = Screen.NuevoProducto.route,
            arguments = listOf(navArgument("compraId") { type = NavType.IntType })
        ) { backStackEntry ->
            val compraId = backStackEntry.arguments?.getInt("compraId") ?: 0
            NuevoProductoScreen(
                compraId = compraId,
                onBack = { navController.popBackStack() },
                onProductoGuardado = {
                    // Cuando se guarda el producto, vamos al detalle de la compra
                    // y limpiamos el back stack hasta Home (para que el usuario
                    // pueda volver con back directo al Home).
                    navController.navigate(Screen.DetalleCompra.createRoute(compraId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // Detalle de una compra: muestra productos, total y permite editar/eliminar.
        composable(
            route = Screen.DetalleCompra.route,
            arguments = listOf(navArgument("compraId") { type = NavType.IntType })
        ) { backStackEntry ->
            val compraId = backStackEntry.arguments?.getInt("compraId") ?: 0
            DetalleCompraScreen(
                compraId = compraId,
                navController = navController
            )
        }
    }
}
