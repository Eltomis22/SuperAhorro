package com.undef.superahorro.Loza.Urieta.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.navigation.Screen
import com.undef.superahorro.Loza.Urieta.ui.components.CompraResumenCard
import com.undef.superahorro.Loza.Urieta.ui.components.SuperAhorroBottomBar
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar
import com.undef.superahorro.Loza.Urieta.ui.screens.home.components.*
import com.undef.superahorro.Loza.Urieta.ui.theme.*
import com.undef.superahorro.Loza.Urieta.ui.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = stringResource(R.string.home_hello, state.usuarioNombre.split(" ").getOrNull(0) ?: ""),
                subtitle = stringResource(R.string.home_subtitle),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Presupuestos.route) }) {
                        Icon(Icons.Filled.AccountBalanceWallet, null)
                    }
                    IconButton(onClick = { /* TODO */ }) {
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
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text(stringResource(R.string.home_new_purchase), fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        PremiumSpendingCard(
                            totalMes = state.totalMes,
                            message = state.budgetMessage
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            ModernActionCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Filled.History,
                                label = stringResource(R.string.home_history),
                                color = ActionHistoryBg,
                                iconColor = ActionHistoryIcon,
                                onClick = { navController.navigate(Screen.HistorialCompras.route) }
                            )
                            ModernActionCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Filled.BarChart,
                                label = stringResource(R.string.home_stats),
                                color = ActionStatsBg,
                                iconColor = ActionStatsIcon,
                                onClick = { navController.navigate(Screen.Estadisticas.route) }
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            ModernActionCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Filled.Storefront,
                                label = stringResource(R.string.bottom_comparativa), // Usamos nueva string
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                iconColor = MaterialTheme.colorScheme.tertiary,
                                onClick = { navController.navigate(Screen.Comparativa.route) }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoMiniCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(R.string.home_label_favorite),
                                value = state.superMasVisitado,
                                icon = Icons.Filled.Storefront,
                                color = InfoFavoriteBg
                            )
                            InfoMiniCard(
                                modifier = Modifier.weight(1f),
                                title = stringResource(R.string.home_label_savings),
                                value = Formatters.formatearMoneda(state.ahorroEstimado),
                                icon = Icons.AutoMirrored.Filled.TrendingUp,
                                color = InfoSavingsBg,
                                onClick = { navController.navigate(Screen.Presupuestos.route) }
                            )
                        }
                    }

                    item { TipCard() }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.home_recent_title),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = stringResource(R.string.home_ver_todo),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { navController.navigate(Screen.HistorialCompras.route) }
                            )
                        }
                    }

                    if (state.ultimasCompras.isEmpty()) {
                        item { EmptyRecentState() }
                    } else {
                        items(state.ultimasCompras, key = { it.id }) { compra ->
                            CompraResumenCard(
                                compra = compra,
                                onClick = {
                                    navController.navigate(Screen.DetalleCompra.createRoute(compra.id))
                                }
                            )
                        }
                    }
                    
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}
