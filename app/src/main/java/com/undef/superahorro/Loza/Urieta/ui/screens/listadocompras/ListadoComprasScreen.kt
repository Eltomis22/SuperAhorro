package com.undef.superahorro.Loza.Urieta.ui.screens.listadocompras

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.navigation.Screen
import com.undef.superahorro.Loza.Urieta.ui.components.CompraResumenCard
import com.undef.superahorro.Loza.Urieta.ui.components.SuperAhorroBottomBar
import com.undef.superahorro.Loza.Urieta.ui.components.CompraResumenCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoComprasScreen(
    navController: NavHostController,
    viewModel: ListadoComprasViewModel = viewModel() // Inyección de tu ViewModel creado en el Paso 2
) {
    // REQUISITO CLAVE: Escuchar el estado respetando el ciclo de vida de la app
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.purchases_list_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = { SuperAhorroBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.NuevaCompra.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, null)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Manejo de flujos (Carga, Error o Lista renderizada)
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = state.error ?: "Error al cargar las compras",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.compras.isEmpty()) {
                Text(
                    text = "No hay compras registradas.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.compras, key = { it.id }) { compra ->
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