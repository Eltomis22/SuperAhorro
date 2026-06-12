package com.undef.superahorro.Loza.Urieta.ui.screens.detallecompra

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import com.undef.superahorro.Loza.Urieta.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCompraScreen(
    compraId: Int,
    navController: NavHostController,
    viewModel: DetalleCompraViewModel = viewModel(factory = DetalleCompraViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(compraId) {
        viewModel.cargarDetalleCompra(compraId)
    }

    val context = LocalContext.current
    // Obtenemos los textos traducidos desde strings.xml
    val shareTemplate = stringResource(R.string.share_purchase_text)
    val shareChooserTitle = stringResource(R.string.share_purchase_chooser)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.purchase_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    // BOTÓN COMPARTIR (REQUISITO INTENTS)
                    state.compra?.let { compra ->
                        IconButton(onClick = {
                            val texto = shareTemplate.format(
                                compra.supermercado,
                                compra.fecha,
                                "%,.0f".format(compra.total)
                            )
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, texto)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, shareChooserTitle))
                        }) {
                            Icon(Icons.Filled.Share, null)
                        }
                    }
                    IconButton(onClick = {
                        state.compra?.let {
                            navController.navigate(Screen.EditarCompra.createRoute(it.id))
                        }
                    }) {
                        Icon(Icons.Filled.Edit, null)
                    }
                    IconButton(onClick = { /* TODO eliminar */ }) {
                        Icon(Icons.Filled.Delete, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            state.compra?.let { compra ->
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.NuevoProducto.createRoute(compra.id)) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Filled.Add, null)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = state.error ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.compra == null) {
                Text(
                    text = "No se encontró la compra seleccionada.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val compra = state.compra!!

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = compra.supermercado,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "${compra.fecha} · ${compra.hora}",
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.label_total),
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "$ %,.0f".format(compra.total),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clickable {
                                    compra.ticketImagenUri?.let { uriString ->
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(Uri.parse(uriString), "image/*")
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Si no hay visor de imágenes o falla
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (compra.ticketImagenUri != null) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Image,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Spacer(Modifier.size(12.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.purchase_detail_ticket),
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = stringResource(R.string.purchase_detail_ticket_hint),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = stringResource(R.string.purchase_detail_products),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (compra.productos.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.purchase_detail_no_products),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(compra.productos, key = { it.id }) { producto ->
                            ProductoItemCard(producto)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoItemCard(producto: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.SemiBold)
                Text(
                    producto.descripcion,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Cód: ${producto.codigo}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "x${producto.cantidad}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "$ %,.0f".format(producto.subtotal),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
