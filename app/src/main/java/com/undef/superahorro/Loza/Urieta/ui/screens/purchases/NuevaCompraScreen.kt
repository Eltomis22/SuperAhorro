package com.undef.superahorro.Loza.Urieta.ui.screens.purchases

import android.Manifest
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar
import com.undef.superahorro.Loza.Urieta.ui.util.Formatters
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCompraScreen(
    onBack: () -> Unit,
    onCompraGuardada: (Int) -> Unit,
    compraIdParaEditar: Int? = null,
    viewModel: NuevaCompraViewModel = viewModel(factory = NuevaCompraViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // --- LÓGICA DE CÁMARA ---
    var ticketUri by remember { mutableStateOf<Uri?>(null) }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) ticketUri = null
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "ticket_${System.currentTimeMillis()}.jpg"
            )
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            ticketUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // --- ESTADO DEL FORMULARIO ---
    var fecha by remember { mutableStateOf(LocalDate.now().toString()) }
    var hora by remember { mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))) }
    var supermercado by remember { mutableStateOf("") }
    
    // Cambiamos 'total' para que guarde el texto crudo (sin puntos) mientras se escribe
    var totalRaw by remember { mutableStateOf("") }
    
    var menuExpanded by remember { mutableStateOf(false) }

    // --- DIÁLOGOS DE FECHA Y HORA ---
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        fecha = selectedDate.toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        DatePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    hora = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        ) {
            Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = timePickerState)
            }
        }
    }

    LaunchedEffect(state.guardadoExitoso) {
        state.guardadoExitoso?.let { id ->
            onCompraGuardada(id)
        }
    }

    LaunchedEffect(compraIdParaEditar) {
        compraIdParaEditar?.let { id ->
            viewModel.cargarCompraParaEditar(id)
        }
    }

    LaunchedEffect(state.compraCargada) {
        state.compraCargada?.let { compra ->
            fecha = compra.fecha
            hora = compra.hora
            supermercado = compra.supermercado
            totalRaw = compra.total.toLong().toString()
            ticketUri = compra.ticketImagenUri?.let { Uri.parse(it) }
        }
    }

    val esEdicion = compraIdParaEditar != null

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = stringResource(
                    if (esEdicion) R.string.edit_purchase_title
                    else R.string.new_purchase_title
                ),
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

            // SUPERMERCADO
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

                val sugerencias = state.supermercados.filter {
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

            // FECHA Y HORA (CON PICKERS)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).clickable { showDatePicker = true }) {
                    OutlinedTextField(
                        value = Formatters.formatearFecha(fecha),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_date)) },
                        leadingIcon = { Icon(Icons.Filled.CalendarToday, null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }

                Box(modifier = Modifier.weight(1f).clickable { showTimePicker = true }) {
                    OutlinedTextField(
                        value = hora,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_time)) },
                        leadingIcon = { Icon(Icons.Filled.Schedule, null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // TOTAL CON SEPARADOR DE MILES FLUIDO
            OutlinedTextField(
                value = totalRaw,
                onValueChange = { input ->
                    // Solo aceptamos números (para evitar que el usuario borre el punto visual y cause líos)
                    if (input.all { it.isDigit() }) {
                        totalRaw = input
                    }
                },
                label = { Text(stringResource(R.string.label_total)) },
                supportingText = { Text(stringResource(R.string.label_total_hint)) },
                leadingIcon = { Icon(Icons.Filled.AttachMoney, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = Formatters.ThousandsSeparatorTransformation(), // El punto es SOLO visual
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // CARD DE CÁMARA
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { permissionLauncher.launch(Manifest.permission.CAMERA) },
                colors = CardDefaults.cardColors(
                    containerColor = if (ticketUri != null) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer
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
                            .background(if (ticketUri != null) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (ticketUri != null) Icons.Filled.CheckCircle else Icons.Filled.AddAPhoto,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (ticketUri != null) "¡Ticket capturado!" else stringResource(R.string.new_purchase_attach_ticket),
                        fontWeight = FontWeight.Bold,
                        color = if (ticketUri != null) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (ticketUri != null) "Toca para cambiar la foto" else stringResource(R.string.new_purchase_attach_hint),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            val totalNumerico = totalRaw.toDoubleOrNull()
            val formularioValido = supermercado.isNotBlank() && totalNumerico != null && totalNumerico > 0

            Button(
                onClick = {
                    viewModel.guardarCompra(
                        id = compraIdParaEditar,
                        fecha = fecha,
                        hora = hora,
                        supermercado = supermercado,
                        total = totalNumerico ?: 0.0,
                        ticketImagenUri = ticketUri?.toString()
                    )
                },
                enabled = formularioValido && !state.isLoading,
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

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
}
