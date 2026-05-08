package com.undef.superahorro.Loza.Urieta.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.data.MockData
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.navigation.Screen
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Tipos de filtro disponibles. Los identificamos por índice para que el cambio
 * de idioma no rompa la comparación (los strings de los chips son traducibles).
 */
private const val FILTRO_TODOS = 0
private const val FILTRO_ESTE_MES = 1
private const val FILTRO_MES_ANTERIOR = 2
private const val FILTRO_CARREFOUR = 3
private const val FILTRO_COTO = 4

/**
 * Historial de compras con filtros funcionales.
 *
 * Filtros disponibles (chips arriba):
 * - Todos:        sin filtro, muestra todas las compras.
 * - Este mes:     compras cuya fecha empieza con el yyyy-MM actual.
 * - Mes anterior: compras del mes anterior.
 * - Carrefour / Coto: filtra por supermercado.
 *
 * Las compras se ordenan por fecha+hora descendente y se agrupan por mes
 * para mostrar un encabezado tipo "Abril 2026" antes de cada bloque.
 *
 * Trick importante: identificamos los filtros por ÍNDICE (Int), no por string,
 * porque el texto del chip cambia según el idioma (ES/EN) y compararlo por
 * string se rompería al cambiar locale.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialComprasScreen(navController: NavHostController) {

    // mutableIntStateOf es la versión específica para Int (más eficiente que
    // mutableStateOf<Int> porque evita boxing).
    var filtroSelected by remember { mutableIntStateOf(FILTRO_TODOS) }

    val filtros = listOf(
        FILTRO_TODOS to stringResource(R.string.filter_all),
        FILTRO_ESTE_MES to stringResource(R.string.filter_this_month),
        FILTRO_MES_ANTERIOR to stringResource(R.string.filter_last_month),
        FILTRO_CARREFOUR to "Carrefour",
        FILTRO_COTO to "Coto"
    )

    val mesesArray = stringArrayResource(R.array.month_names)

    // Prefijos yyyy-MM para filtros relativos a la fecha actual
    val (prefijoEsteMes, prefijoMesAnterior) = remember {
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM")
        val hoy = LocalDate.now()
        hoy.format(fmt) to hoy.minusMonths(1).format(fmt)
    }

    // ----------------------------------------------------------------
    // APLICAR FILTRO + ordenar
    // ----------------------------------------------------------------
    // sortedByDescending ordena de más reciente a más antigua usando
    // fecha+hora concatenadas (string compare funciona porque están en
    // formato yyyy-MM-dd HH:mm que se ordena lexicográficamente igual que cronológicamente).
    val comprasOrdenadas = MockData.compras.sortedByDescending { it.fecha + it.hora }
    val comprasFiltradas: List<Compra> = when (filtroSelected) {
        FILTRO_TODOS -> comprasOrdenadas
        FILTRO_ESTE_MES -> comprasOrdenadas.filter { it.fecha.startsWith(prefijoEsteMes) }
        FILTRO_MES_ANTERIOR -> comprasOrdenadas.filter { it.fecha.startsWith(prefijoMesAnterior) }
        FILTRO_CARREFOUR -> comprasOrdenadas.filter { it.supermercado == "Carrefour" }
        FILTRO_COTO -> comprasOrdenadas.filter { it.supermercado == "Coto" }
        else -> comprasOrdenadas
    }

    val agrupadas = comprasFiltradas.groupBy { it.fecha.substring(0, 7) }

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = stringResource(R.string.history_title),
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Chips de filtros
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filtros) { (id, label) ->
                    AssistChip(
                        onClick = { filtroSelected = id },
                        label = { Text(label) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (filtroSelected == id)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surface,
                            labelColor = if (filtroSelected == id)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            // Lista de compras agrupadas por mes
            if (comprasFiltradas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(64.dp))
                    Text(
                        text = stringResource(R.string.history_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    agrupadas.forEach { (mes, compras) ->
                        item {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = formatMes(mes, mesesArray),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(compras, key = { it.id }) { compra ->
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
        }
    }
}

/**
 * Formatea "2026-04" a "Abril 2026" / "April 2026" según el array de meses
 * provisto desde strings.xml (respeta el idioma activo).
 */
private fun formatMes(yyyyMm: String, meses: Array<String>): String {
    val partes = yyyyMm.split("-")
    val mesIndex = partes.getOrNull(1)?.toIntOrNull()?.minus(1) ?: return yyyyMm
    val mes = meses.getOrNull(mesIndex) ?: return yyyyMm
    return "$mes ${partes[0]}"
}
