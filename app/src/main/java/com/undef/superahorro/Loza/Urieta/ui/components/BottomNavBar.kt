package com.undef.superahorro.Loza.Urieta.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.navigation.Screen

private data class BottomItem(
    val route: String,
    val icon: ImageVector,
    val labelRes: Int
)

private val bottomItems = listOf(
    BottomItem(Screen.Home.route, Icons.Filled.Home, R.string.bottom_home),
    BottomItem(Screen.ListadoCompras.route, Icons.Filled.ShoppingCart, R.string.bottom_compras),
    BottomItem(Screen.Estadisticas.route, Icons.Filled.BarChart, R.string.bottom_estadisticas),
    BottomItem(Screen.MiPerfil.route, Icons.Filled.Person, R.string.bottom_perfil)
)

@Composable
fun SuperAhorroBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        bottomItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Si tocan Home: limpiamos el back stack hasta Home (inclusive)
                            // y entramos a Home limpio. Para los otros tabs: pop hasta Home
                            // sin removerlo, así el Home siempre está abajo en la pila.
                            popUpTo(Screen.Home.route) {
                                inclusive = (item.route == Screen.Home.route)
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.labelRes)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
