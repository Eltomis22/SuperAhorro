package com.undef.superahorro.Loza.Urieta.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.undef.superahorro.Loza.Urieta.ui.screens.auth.ForgotPasswordScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.auth.LoginScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.auth.RegisterScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.auth.SplashScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.detallecompra.DetalleCompraScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.estadisticas.EstadisticasScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.home.HomeScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.listadocompras.ListadoComprasScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.miperfil.MiPerfilScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.purchases.HistorialComprasScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.purchases.NuevaCompraScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.purchases.NuevoProductoScreen
import com.undef.superahorro.Loza.Urieta.ui.screens.settings.SettingsScreen

/**
 * Grafo de navegación principal de la aplicación.
 */
@Composable
fun SuperAhorroNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // ================================================================
        // FLUJO DE AUTENTICACIÓN
        // ================================================================

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
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

        composable(Screen.NuevaCompra.route) {
            NuevaCompraScreen(
                onBack = { navController.popBackStack() },
                onCompraGuardada = { compraId ->
                    navController.navigate(Screen.NuevoProducto.createRoute(compraId)) {
                        popUpTo(Screen.NuevaCompra.route) { inclusive = true }
                    }
                }
            )
        }

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

        composable(
            route = Screen.NuevoProducto.route,
            arguments = listOf(navArgument("compraId") { type = NavType.IntType })
        ) { backStackEntry ->
            val compraId = backStackEntry.arguments?.getInt("compraId") ?: 0
            NuevoProductoScreen(
                compraId = compraId,
                onBack = { navController.popBackStack() },
                onProductoGuardado = {
                    navController.navigate(Screen.DetalleCompra.createRoute(compraId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

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
