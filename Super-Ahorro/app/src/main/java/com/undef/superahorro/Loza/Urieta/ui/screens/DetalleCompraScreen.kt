package com.undef.superahorro.Loza.Urieta.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.data.MockData
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import com.undef.superahorro.Loza.Urieta.navigation.Screen
import com.undef.superahorro.Loza.Urieta.ui.components.ConfirmDialog
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar
import com.undef.superahorro.Loza.Urieta.ui.util.Formatters
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCompraScreen(
    compraId: Int,
    navController: NavHostController
) {

    // Si la compra fue eliminada, evitamos crashear: salimos de la pantalla
    val compra = MockData.compras.firstOrNull { it.id == compraId }
    if (compra == null) {
        // popBack lazy: cuando se borra la compra desde acá, este side-effect cierra la screen
        androidx.compose.runtime.LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val context = LocalContext.current
    val shareTemplate = stringResource(R.string.share_purchase_text)
    val shareChooserTitle = stringResource(R.string.share_purchase_chooser)

    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = stringResource(R.string.purchase_detail_title),
                onBack = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = {
                        val texto = shareTemplate.format(
                            compra.supermercado,
                            compra.fecha,
                            Formatters.formatearMiles(compra.total.toLong().toString())
                        )
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, texto)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, shareChooserTitle))
                    }) {
                        Icon(Icons.Filled.Share, null)
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.EditarCompra.createRoute(compra.id))
                    }) {
                        Icon(Icons.Filled.Edit, null)
                    }
                    IconButton(onClick = { mostrarDialogoEliminar = true }) {
                        Icon(Icons.Filled.Delete, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.NuevoProducto.createRoute(compra.id)) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, null)
            }
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
                // Header con resumen
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
                            text = Formatters.formatearMoneda(compra.total),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                // Card del ticket
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
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
                    ProductoItemCard(
                        producto = producto,
                        onEliminar = { productoAEliminar = producto }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación: eliminar compra
    if (mostrarDialogoEliminar) {
        ConfirmDialog(
            title = stringResource(R.string.dialog_delete_purchase_title),
            message = stringResource(R.string.dialog_delete_purchase_msg),
            confirmText = stringResource(R.string.action_delete),
            onConfirm = {
                mostrarDialogoEliminar = false
                MockData.eliminarCompra(compra.id)
                navController.popBackStack()
            },
            onDismiss = { mostrarDialogoEliminar = false }
        )
    }

    // Diálogo de confirmación: eliminar producto
    productoAEliminar?.let { p ->
        ConfirmDialog(
            title = stringResource(R.string.dialog_delete_product_title),
            message = p.nombre,
            confirmText = stringResource(R.string.action_delete),
            onConfirm = {
                MockData.eliminarProducto(compra.id, p.id)
                productoAEliminar = null
            },
            onDismiss = { productoAEliminar = null }
        )
    }
}

@Composable
private fun ProductoItemCard(
    producto: Producto,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 14.dp, bottom = 14.dp, end = 4.dp),
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
                    Formatters.formatearMoneda(producto.subtotal),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
