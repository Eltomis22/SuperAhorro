package com.undef.superahorro.Loza.Urieta.ui.screens.purchases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar
import com.undef.superahorro.Loza.Urieta.ui.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoProductoScreen(
    compraId: Int,
    onBack: () -> Unit,
    onProductoGuardado: () -> Unit,
    viewModel: NuevoProductoViewModel = viewModel(factory = NuevoProductoViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) {
            onProductoGuardado()
        }
    }

    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("1") }
    
    // Cambiamos a 'precioRaw' para evitar saltos del cursor
    var precioRaw by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = stringResource(R.string.new_product_title),
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

            Text(
                text = stringResource(R.string.new_product_subtitle, compraId),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text(stringResource(R.string.label_code)) },
                leadingIcon = { Icon(Icons.Filled.QrCode, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(stringResource(R.string.label_product_name)) },
                leadingIcon = { Icon(Icons.Filled.ShoppingBasket, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text(stringResource(R.string.label_description)) },
                leadingIcon = { Icon(Icons.Filled.Description, null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            cantidad = input
                        }
                    },
                    label = { Text(stringResource(R.string.label_quantity)) },
                    leadingIcon = { Icon(Icons.Filled.Numbers, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.8f)
                )
                OutlinedTextField(
                    value = precioRaw,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            precioRaw = input
                        }
                    },
                    label = { Text(stringResource(R.string.label_price)) },
                    leadingIcon = { Icon(Icons.Filled.AttachMoney, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = Formatters.ThousandsSeparatorTransformation(), // Fix para el cursor
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(Modifier.height(24.dp))

            val precioNumerico = precioRaw.toDoubleOrNull()
            val cantidadNumerica = cantidad.toIntOrNull() ?: 1
            val formularioValido = nombre.isNotBlank() && precioNumerico != null && precioNumerico > 0

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(
                    onClick = {
                        viewModel.guardarProducto(
                            compraId = compraId,
                            codigo = codigo.ifBlank { "—" },
                            nombre = nombre,
                            descripcion = descripcion,
                            cantidad = cantidadNumerica,
                            precio = precioNumerico ?: 0.0
                        )
                    },
                    enabled = formularioValido && !state.isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        stringResource(R.string.action_save),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
