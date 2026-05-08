package com.undef.superahorro.Loza.Urieta.ui.screens

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar
import com.undef.superahorro.Loza.Urieta.ui.util.Formatters
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.data.MockData
import com.undef.superahorro.Loza.Urieta.ui.components.SuperAhorroBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(navController: NavHostController) {

    val gastoMensual = MockData.gastoMensual
    val gastoSuper = MockData.gastoPorSupermercado
    val totalGastado = gastoSuper.sumOf { it.second }

    Scaffold(
        topBar = {
            SuperTopAppBar(title = stringResource(R.string.stats_title))
        },
        bottomBar = { SuperAhorroBottomBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // KPIs
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KPICard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.stats_kpi_total),
                        value = Formatters.formatearMoneda(totalGastado)
                    )
                    KPICard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.stats_kpi_purchases),
                        value = MockData.compras.size.toString()
                    )
                }
            }

            item {
                ChartCard(title = stringResource(R.string.stats_monthly_evolution)) {
                    BarChart(data = gastoMensual)
                }
            }

            item {
                ChartCard(title = stringResource(R.string.stats_by_supermarket)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        gastoSuper.forEach { (sm, monto) ->
                            ProgressRow(label = sm, valor = monto, max = totalGastado)
                        }
                    }
                }
            }

            item {
                ChartCard(title = stringResource(R.string.stats_top_products)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "Leche La Serenísima" to 8,
                            "Pan Lactal Bimbo" to 6,
                            "Yerba Taragüí" to 4,
                            "Coca Cola 2.25L" to 4,
                            "Aceite Natura" to 3
                        ).forEachIndexed { idx, (nombre, cant) ->
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
                                        text = "${idx + 1}",
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

@Composable
private fun KPICard(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
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
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

/**
 * Gráfico de barras simple dibujado con Canvas.
 * Sin dependencias externas para que la 1ra entrega compile sin librerías de charts.
 */
@Composable
private fun BarChart(data: List<Pair<String, Double>>) {
    val maxValue = data.maxOfOrNull { it.second } ?: 1.0
    val barColor = MaterialTheme.colorScheme.primary

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val barWidth = size.width / (data.size * 2f)
            data.forEachIndexed { i, (_, value) ->
                val barHeight = (value / maxValue).toFloat() * size.height
                val x = (i * 2 + 1) * barWidth - barWidth / 2
                drawRect(
                    brush = SolidColor(barColor),
                    topLeft = Offset(x, size.height - barHeight),
                    size = Size(barWidth, barHeight)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            data.forEach { (label, _) ->
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProgressRow(label: String, valor: Double, max: Double) {
    val pct = if (max > 0) (valor / max).toFloat() else 0f
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label)
            Text(
                Formatters.formatearMoneda(valor),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(pct)
                    .height(8.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
