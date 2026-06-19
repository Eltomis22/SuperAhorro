package com.undef.superahorro.Loza.Urieta.ui.screens.miperfil

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CambiarEmailScreen(
    onBack: () -> Unit,
    viewModel: MiPerfilViewModel = viewModel(factory = MiPerfilViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var nuevoEmail by remember { mutableStateOf("") }

    LaunchedEffect(state.usuario) {
        state.usuario?.let { nuevoEmail = it.email }
    }

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = stringResource(R.string.profile_change_email),
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            OutlinedTextField(
                value = nuevoEmail,
                onValueChange = { nuevoEmail = it },
                label = { Text("Nuevo Email") },
                leadingIcon = { Icon(Icons.Filled.Email, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            val emailValido = nuevoEmail.contains("@") && nuevoEmail.contains(".")

            Button(
                onClick = {
                    viewModel.actualizarEmail(nuevoEmail)
                    onBack()
                },
                enabled = emailValido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Actualizar Email", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
