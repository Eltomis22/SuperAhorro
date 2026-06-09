package com.undef.superahorro.Loza.Urieta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.data.MockData
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar
import com.undef.superahorro.Loza.Urieta.ui.util.Formatters
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCompraScreen(
    onBack: () -> Unit,
    onCompraGuardada: (Int) -> Unit,
    compraIdParaEditar: Int? = null
) {

    val compraExistente = remember(compraIdParaEditar) {
        compraIdParaEditar?.let { id -> MockData.compras.firstOrNull { it.id == id } }
    }
    val esEdicion = compraExistente != null

    // Por defecto fecha y hora actuales del dispositivo
    val hoy = remember { LocalDate.now().toString() }
    val ahora = remember {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    var fecha by remember { mutableStateOf(compraExistente?.fecha ?: hoy) }
    var hora by remember { mutableStateOf(compraExistente?.hora ?: ahora) }
    var supermercado by remember { mutableStateOf(compraExistente?.supermercado ?: "") }
    var total by remember {
        mutableStateOf(
            compraExistente?.let { Formatters.formatearMiles(it.total.toLong().toString()) } ?: ""
        )
    }
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = stringResource(
                    if (esEdicion) R.string.edit_purchase_title
                    else R.string.new_purchase_title
                ),
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            ExposedDropdownMenuBox(
                expanded = menuExpanded,
                onExpandedChange = { menuExpanded = it }
            ) {
                OutlinedTextField(
                    value = supermercado,
                    onValueChange = {
                        supermercado = it
                        menuExpanded = true
                    },
                    label = { Text(stringResource(R.string.label_supermarket)) },
                    leadingIcon = { Icon(Icons.Filled.Store, null) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                        .fillMaxWidth()
                )

                val sugerencias = MockData.supermercados.filter {
                    it.contains(supermercado, ignoreCase = true)
                }
                if (sugerencias.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        sugerencias.forEach { sm ->
                            DropdownMenuItem(
                                text = { Text(sm) },
                                onClick = {
                                    supermercado = sm
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text(stringResource(R.string.label_date)) },
                    leadingIcon = { Icon(Icons.Filled.CalendarToday, null) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = { Text(stringResource(R.string.label_time)) },
                    leadingIcon = { Icon(Icons.Filled.Schedule, null) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // El usuario tipea "1000" y se ve "1.000" al instante.

            OutlinedTextField(
                value = total,
                onValueChange = { input ->
                    // Format con separador de miles mientras el usuario tipea (1000 -> 1.000)
                    total = Formatters.formatearMiles(input)
                },
                label = { Text(stringResource(R.string.label_total)) },
                supportingText = { Text(stringResource(R.string.label_total_hint)) },
                leadingIcon = { Icon(Icons.Filled.AttachMoney, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Card para adjuntar ticket
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { /* TODO: galería/cámara con Intent */ },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddAPhoto,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.new_purchase_attach_ticket),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(R.string.new_purchase_attach_hint),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))


            // El botón se habilita solo si hay supermercado y un total numérico válido.
            val totalNumerico = Formatters.parsearMiles(total)
            val formularioValido = supermercado.isNotBlank() && totalNumerico != null && totalNumerico > 0


            Button(
                onClick = {
                    if (esEdicion && compraExistente != null) {
                        // MODO EDICIÓN: actualizamos la compra conservando sus productos.
                        val actualizada = compraExistente.copy(
                            fecha = fecha,
                            hora = hora,
                            supermercado = supermercado,
                            total = totalNumerico ?: 0.0
                        )
                        MockData.actualizarCompra(actualizada)
                        onCompraGuardada(actualizada.id)
                    } else {
                        // MODO CREACIÓN: damos de alta una compra nueva con id auto-generado.
                        val nuevaCompra = Compra(
                            id = MockData.siguienteIdCompra(),
                            fecha = fecha,
                            hora = hora,
                            supermercado = supermercado,
                            total = totalNumerico ?: 0.0,
                            productos = emptyList()
                        )
                        MockData.agregarCompra(nuevaCompra)
                        onCompraGuardada(nuevaCompra.id)
                    }
                },
                enabled = formularioValido,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = stringResource(
                        if (esEdicion) R.string.edit_purchase_save
                        else R.string.new_purchase_save_and_continue
                    ),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
