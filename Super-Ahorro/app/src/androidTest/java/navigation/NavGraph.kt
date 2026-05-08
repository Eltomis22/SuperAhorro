package navigation

class NavGraph {

    package com.undef.superahorro.navigation

    import androidx.compose.runtime.Composable
    import androidx.navigation.NavHostController
    import androidx.navigation.compose.*

    import com.undef.superahorro.ui.screens.*

    @Composable
    fun NavGraph(navController: NavHostController) {

        NavHost(
            navController = navController,
            startDestination = "splash"
        ) {

            composable("splash") {
                SplashScreen(navController)
            }

            composable("login") {
                LoginScreen(navController)
            }

            composable("home") {
                HomeScreen(navController)
            }

            composable("nueva_compra") {
                NuevaCompraScreen(navController)
            }

            composable("lista_compras") {
                ListaComprasScreen(navController)
            }

            composable("detalle_compra") {
                DetalleCompraScreen(navController)
            }
        }
    }

}