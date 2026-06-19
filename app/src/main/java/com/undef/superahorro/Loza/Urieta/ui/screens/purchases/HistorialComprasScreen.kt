package com.undef.superahorro.Loza.Urieta.ui.screens.purchases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val FILTRO_TODOS = 0
private const val FILTRO_ESTE_MES = 1
private const val FILTRO_MES_ANTERIOR = 2
private const val FILTRO_CARREFOUR = 3
private const val FILTRO_COTO = 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialComprasScreen(
    navController: NavHostController,
    viewModel: HistorialComprasViewModel = viewModel(factory = HistorialComprasViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var filtroSelected by remember { mutableIntStateOf(FILTRO_TODOS) }

    val filtros = listOf(
        FILTRO_TODOS to stringResource(R.string.filter_all),
        FILTRO_ESTE_MES to stringResource(R.string.filter_this_month),
        FILTRO_MES_ANTERIOR to stringResource(R.string.filter_last_month),
        FILTRO_CARREFOUR to "Carrefour",
        FILTRO_COTO to "Coto"
    )

    val mesesArray = stringArrayResource(R.array.month_names)

    val (prefijoEsteMes, prefijoMesAnterior) = remember {
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM")
        val hoy = LocalDate.now()
        hoy.format(fmt) to hoy.minusMonths(1).format(fmt)
    }

    val agrupadasFiltradas = state.comprasAgrupadas.filter { (mes, _) ->
        when (filtroSelected) {
            FILTRO_ESTE_MES -> mes == prefijoEsteMes
            FILTRO_MES_ANTERIOR -> mes == prefijoMesAnterior
            else -> true
        }
    }.mapValues { (_, compras) ->
        when (filtroSelected) {
            FILTRO_CARREFOUR -> compras.filter { it.supermercado == "Carrefour" }
            FILTRO_COTO -> compras.filter { it.supermercado == "Coto" }
            else -> compras
        }
    }.filter { it.value.isNotEmpty() }

    Scaffold(
        topBar = {
            SuperTopAppBar(title = stringResource(R.string.history_title))
        },
        bottomBar = { SuperAhorroBottomBar(navController) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
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

                if (agrupadasFiltradas.isEmpty() && !state.isLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(64.dp))
                        Text(
                            text = if (state.error != null) state.error!! 
                                   else stringResource(R.string.history_empty),
                            color = if (state.error != null) MaterialTheme.colorScheme.error 
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        agrupadasFiltradas.forEach { (mes, compras) ->
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

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

private fun formatMes(yyyyMm: String, meses: Array<String>): String {
    val partes = yyyyMm.split("-")
    val mesIndex = partes.getOrNull(1)?.toIntOrNull()?.minus(1) ?: return yyyyMm
    val mes = meses.getOrNull(mesIndex) ?: return yyyyMm
    return "$mes ${partes[0]}"
}
