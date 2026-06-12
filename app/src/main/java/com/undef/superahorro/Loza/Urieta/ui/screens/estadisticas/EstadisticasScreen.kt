package com.undef.superahorro.Loza.Urieta.ui.screens.estadisticas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.ui.components.SuperAhorroBottomBar
import com.undef.superahorro.Loza.Urieta.ui.util.ColorUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    navController: NavHostController,
    viewModel: EstadisticasViewModel = viewModel(factory = EstadisticasViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val totalGastado = state.gastoPorSupermercado.sumOf { it.second }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.stats_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = { SuperAhorroBottomBar(navController) }
    ) { padding ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: ${state.error}", color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        KPICard(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.stats_kpi_total),
                            value = "$ %,.0f".format(totalGastado)
                        )
                        KPICard(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.stats_kpi_purchases),
                            value = state.gastoMensual.size.toString()
                        )
                    }
                }

                item {
                    ChartCard(title = stringResource(R.string.stats_monthly_evolution)) {
                        if (state.gastoMensual.isEmpty()) {
                            Text("No hay datos aún.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            // Usamos un color contrastante (Violeta/Indigo) para que resalte del verde
                            ModernBarChart(data = state.gastoMensual)
                        }
                    }
                }

                item {
                    ChartCard(title = stringResource(R.string.stats_by_supermarket)) {
                        if (state.gastoPorSupermercado.isEmpty()) {
                            Text("No hay datos aún.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                state.gastoPorSupermercado.forEach { (sm, monto) ->
                                    ProgressRow(
                                        label = sm, 
                                        valor = monto, 
                                        max = totalGastado,
                                        accentColor = ColorUtils.getColorForName(sm)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    ChartCard(title = stringResource(R.string.stats_top_products)) {
                        if (state.productosMasComprados.isEmpty()) {
                            Text("No hay productos aún.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                state.productosMasComprados.forEachIndexed { idx, (nombre, cant) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = (idx + 1).toString(),
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(Modifier.size(12.dp))
                                        Text(nombre, modifier = Modifier.weight(1f))
                                        Text(
                                            "${cant}x",
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KPICard(modifier: Modifier = Modifier, label: String, value: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
            Spacer(Modifier.height(4.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ModernBarChart(data: List<Pair<String, Double>>) {
    val maxValue = data.maxOfOrNull { it.second } ?: 1.0
    
    // Animación de entrada
    var animationTriggered by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )
    
    LaunchedEffect(Unit) {
        animationTriggered = true
    }

    // Usamos un gradiente Indigo/Violeta para que resalte del verde de la app
    val barBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF5C6BC0), Color(0xFF3949AB))
    )

    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            val barWidth = size.width / (data.size * 2f)
            data.forEachIndexed { i, (_, value) ->
                val barHeight = ((value / maxValue).toFloat() * size.height) * progress
                val x = (i * 2 + 1) * barWidth - barWidth / 2
                
                drawRoundRect(
                    brush = barBrush,
                    topLeft = Offset(x, size.height - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            data.forEach { (label, _) ->
                Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ProgressRow(label: String, valor: Double, max: Double, accentColor: Color) {
    val pct = if (max > 0) (valor / max).toFloat() else 0f
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 14.sp)
            Text("$ %,.0f".format(valor), fontWeight = FontWeight.Bold, color = accentColor)
        }
        Spacer(Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(accentColor.copy(alpha = 0.1f))) {
            Box(modifier = Modifier.fillMaxWidth(pct).height(10.dp).background(accentColor))
        }
    }
}
