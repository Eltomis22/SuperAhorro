package com.undef.superahorro.Loza.Urieta.ui.screens.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresupuestosScreen(
    onBack: () -> Unit,
    viewModel: PresupuestosViewModel = viewModel(factory = PresupuestosViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = "Mis Presupuestos",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.guardar() }) {
                Icon(Icons.Filled.Save, null)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Define tus límites de gasto mensual para cada categoría. El sistema te avisará si un gasto pone en riesgo tus finanzas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(state.presupuestos) { item ->
                        BudgetInputCard(
                            categoria = item.categoria,
                            monto = item.montoMaximo,
                            onValueChange = { viewModel.actualizarLimite(item.categoria, it) }
                        )
                    }
                    
                    if (state.guardadoExitoso) {
                        item {
                            Text(
                                "¡Presupuestos guardados correctamente!",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetInputCard(
    categoria: String,
    monto: Double,
    onValueChange: (Double) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(categoria, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = if (monto == 0.0) "" else monto.toLong().toString(),
                onValueChange = { 
                    val valInt = it.toDoubleOrNull() ?: 0.0
                    onValueChange(valInt)
                },
                label = { Text("Límite mensual ($)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
    }
}
