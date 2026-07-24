package com.undef.superahorro.Loza.Urieta.ui.screens.purchases

import android.Manifest
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.content.pm.PackageManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar
import com.undef.superahorro.Loza.Urieta.ui.theme.BankerSafe
import com.undef.superahorro.Loza.Urieta.ui.theme.BankerSafeBg
import com.undef.superahorro.Loza.Urieta.ui.theme.BankerSafeText
import com.undef.superahorro.Loza.Urieta.ui.theme.BankerUnsafe
import com.undef.superahorro.Loza.Urieta.ui.theme.BankerUnsafeBg
import com.undef.superahorro.Loza.Urieta.ui.theme.BankerUnsafeText
import com.undef.superahorro.Loza.Urieta.ui.theme.TicketCapturedBg
import com.undef.superahorro.Loza.Urieta.ui.theme.TicketCapturedIcon
import com.undef.superahorro.Loza.Urieta.ui.theme.TicketCapturedText
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

    // --- LÓGICA DE CÁMARA MEJORADA ---
    // Guardamos la URI como String para que sea 100% compatible con rememberSaveable
    var ticketUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val ticketUri = ticketUriString?.let { Uri.parse(it) }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            // Si falló y no teníamos una foto previa, limpiamos
            if (state.compraCargada == null) ticketUriString = null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                if (directory == null) {
                    Toast.makeText(context, "Error: No se pudo acceder al almacenamiento", Toast.LENGTH_SHORT).show()
                    return@rememberLauncherForActivityResult
                }
                
                val file = File(directory, "ticket_${System.currentTimeMillis()}.jpg")
                val authority = "com.undef.superahorro.fileprovider"
                val uri = FileProvider.getUriForFile(context, authority, file)
                ticketUriString = uri.toString()
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al preparar la cámara: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleCameraClick() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            try {
                val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                if (directory != null && !directory.exists()) directory.mkdirs()
                
                val file = File(directory, "ticket_${System.currentTimeMillis()}.jpg")
                // AUTORIDAD EN MINÚSCULAS PARA EVITAR CRASHES DEL SISTEMA
                val authority = "com.undef.superahorro.fileprovider"
                val uri = FileProvider.getUriForFile(context, authority, file)
                
                ticketUriString = uri.toString()
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                android.util.Log.e("CameraError", "Error fatal al abrir camara", e)
                Toast.makeText(context, "Error al abrir la cámara. Revisa los permisos.", Toast.LENGTH_LONG).show()
            }
        } else {
            permissionLauncher.launch(permission)
        }
    }

    // --- ESTADO DEL FORMULARIO ---
    var fecha by remember { mutableStateOf(LocalDate.now().toString()) }
    var hora by remember { mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))) }
    var supermercado by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Otros") }
    
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
                }) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.action_cancel)) }
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
                }) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.action_cancel)) }
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
            fecha = compra.fecha ?: LocalDate.now().toString()
            hora = compra.hora ?: LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            supermercado = compra.supermercado ?: ""
            categoria = compra.categoria ?: "Otros"
            totalRaw = compra.total.toLong().toString()
            ticketUriString = compra.ticketImagenUri
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

            // CATEGORÍA (Dropdown)
            var catMenuExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = catMenuExpanded,
                onExpandedChange = { catMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_category)) },
                    leadingIcon = { Icon(Icons.Filled.Store, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catMenuExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = catMenuExpanded,
                    onDismissRequest = { catMenuExpanded = false }
                ) {
                    state.categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                categoria = cat
                                catMenuExpanded = false
                            }
                        )
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

            val totalNumerico = totalRaw.toDoubleOrNull()
            
            // --- SIMULADOR DEL BANQUERO ---
            if (totalNumerico != null && totalNumerico > 0) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.verificarGastoSeguro(categoria, totalNumerico) },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.action_verify_budget))
                }

                state.budgetStatus?.let { (safe, message) ->
                    Card(
                        modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (safe) BankerSafeBg else BankerUnsafeBg
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (safe) Icons.Filled.CheckCircle else Icons.Filled.AttachMoney,
                                contentDescription = null,
                                tint = if (safe) BankerSafe else BankerUnsafe
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = message,
                                fontSize = 13.sp,
                                color = if (safe) BankerSafeText else BankerUnsafeText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // CARD DE CÁMARA
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { handleCameraClick() },
                colors = CardDefaults.cardColors(
                    containerColor = if (ticketUri != null) TicketCapturedBg else MaterialTheme.colorScheme.primaryContainer
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
                            .background(if (ticketUri != null) TicketCapturedIcon else MaterialTheme.colorScheme.primary),
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
                        text = if (ticketUri != null) stringResource(R.string.new_purchase_ticket_captured) else stringResource(R.string.new_purchase_attach_ticket),
                        fontWeight = FontWeight.Bold,
                        color = if (ticketUri != null) TicketCapturedText else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (ticketUri != null) stringResource(R.string.new_purchase_change_ticket) else stringResource(R.string.new_purchase_attach_hint),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            val formularioValido = supermercado.isNotBlank() && totalNumerico != null && totalNumerico > 0

            Button(
                onClick = {
                    viewModel.guardarCompra(
                        id = compraIdParaEditar,
                        fecha = fecha,
                        hora = hora,
                        supermercado = supermercado,
                        total = totalNumerico ?: 0.0,
                        categoria = categoria,
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
