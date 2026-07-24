package com.undef.superahorro.Loza.Urieta.ui.screens.purchases

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.undef.superahorro.Loza.Urieta.ui.util.ExportHelper
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
    val context = LocalContext.current

    var filtroSelected by remember { mutableIntStateOf(FILTRO_TODOS) }
    var busquedaTexto by remember { mutableStateOf("") }
    var precioMinimo by remember { mutableStateOf("") }

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

    // LÓGICA DE FILTRADO AVANZADO
    val agrupadasFiltradas = state.comprasAgrupadas.filter { (mes, _) ->
        when (filtroSelected) {
            FILTRO_ESTE_MES -> mes == prefijoEsteMes
            FILTRO_MES_ANTERIOR -> mes == prefijoMesAnterior
            else -> true
        }
    }.mapValues { (_, compras) ->
        compras.filter { compra ->
            val coincideSuper = when (filtroSelected) {
                FILTRO_CARREFOUR -> compra.supermercado == "Carrefour"
                FILTRO_COTO -> compra.supermercado == "Coto"
                else -> true
            }
            val coincideBusqueda = compra.supermercado?.contains(busquedaTexto, ignoreCase = true) == true ||
                                 compra.categoria?.contains(busquedaTexto, ignoreCase = true) == true
            
            val pMin = precioMinimo.toDoubleOrNull() ?: 0.0
            val coincidePrecio = compra.total >= pMin

            coincideSuper && coincideBusqueda && coincidePrecio
        }
    }.filter { it.value.isNotEmpty() }

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = stringResource(R.string.history_title),
                actions = {
                    IconButton(onClick = { 
                        val todas = state.comprasAgrupadas.values.flatten()
                        ExportHelper.exportarComprasCSV(context, todas) 
                    }) {
                        Icon(Icons.Filled.FileDownload, "Exportar CSV")
                    }
                }
            )
        },
        bottomBar = { SuperAhorroBottomBar(navController) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // BUSCADOR AVANZADO
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = busquedaTexto,
                        onValueChange = { busquedaTexto = it },
                        placeholder = { Text("Categoría o Super...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Filled.Search, null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = precioMinimo,
                        onValueChange = { if (it.all { c -> c.isDigit() }) precioMinimo = it },
                        placeholder = { Text("Mín $", fontSize = 12.sp) },
                        modifier = Modifier.width(100.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
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
