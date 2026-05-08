package com.undef.superahorro.Loza.Urieta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.data.MockData
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.navigation.Screen
import com.undef.superahorro.Loza.Urieta.ui.components.SuperAhorroBottomBar
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar
import com.undef.superahorro.Loza.Urieta.ui.util.Formatters

/**
 * Pantalla principal de la app (post-login).
 *
 * Estructura:
 * - TopBar con saludo + botones de notificaciones y settings.
 * - Card verde "Gasto del mes" calculado dinámicamente con LocalDate.now().
 * - Dos accesos rápidos: Historial y Estadísticas.
 * - Listado de las 3 últimas compras (LazyColumn reactivo).
 * - FAB "Nueva compra" para crear una compra rápida.
 * - BottomBar para navegar a Compras / Estadísticas / Perfil.
 *
 * Observa MockData.compras (mutableStateListOf), por lo que cuando se agrega
 * o elimina una compra desde otra pantalla, esta se recompone automáticamente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {

    val usuario = MockData.usuarioActual
    // .take(3) toma sólo las 3 primeras (las más recientes, porque MockData
    // las inserta al principio con add(0, ...)).
    val ultimasCompras = MockData.compras.take(3)

    // Total del mes calendario actual (usa LocalDate.now).
    // Antes estaba hardcodeado en "2026-04" → bug. Ahora calcula el prefijo
    // del mes actual y suma sólo las compras de ese mes.
    val prefijoEsteMes = remember {
        java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"))
    }
    val totalMes = MockData.compras
        .filter { it.fecha.startsWith(prefijoEsteMes) }
        .sumOf { it.total }

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = stringResource(R.string.home_hello, usuario.nombre.split(" ")[0]),
                subtitle = stringResource(R.string.home_subtitle),
                actions = {
                    IconButton(onClick = { /* TODO: notifications */ }) {
                        Icon(Icons.Filled.Notifications, null)
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Filled.Settings, null)
                    }
                }
            )
        },
        bottomBar = { SuperAhorroBottomBar(navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.NuevaCompra.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text(stringResource(R.string.home_new_purchase)) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MonthlyCard(totalMes = totalMes)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.History,
                        label = stringResource(R.string.home_history),
                        onClick = { navController.navigate(Screen.HistorialCompras.route) }
                    )
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.BarChart,
                        label = stringResource(R.string.home_stats),
                        onClick = { navController.navigate(Screen.Estadisticas.route) }
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.home_recent_title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(ultimasCompras, key = { it.id }) { compra ->
                CompraResumenCard(
                    compra = compra,
                    onClick = {
                        navController.navigate(Screen.DetalleCompra.createRoute(compra.id))
                    }
                )
            }
        }
    }
}

@Composable
private fun MonthlyCard(totalMes: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.home_monthly_title),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = Formatters.formatearMoneda(totalMes),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_monthly_hint),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun CompraResumenCard(
    compra: Compra,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Store,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = compra.supermercado,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${compra.fecha} · ${compra.hora}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = Formatters.formatearMoneda(compra.total),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
