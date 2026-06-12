package com.undef.superahorro.Loza.Urieta.ui.screens.miperfil

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.Loza.Urieta.R
import com.undef.superahorro.Loza.Urieta.ui.components.SuperTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CambiarClaveScreen(
    onBack: () -> Unit,
    viewModel: MiPerfilViewModel = viewModel(factory = MiPerfilViewModel.Factory)
) {
    var nuevaClave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SuperTopAppBar(
                title = stringResource(R.string.profile_change_password),
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
                value = nuevaClave,
                onValueChange = { nuevaClave = it },
                label = { Text("Nueva Contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmarClave,
                onValueChange = { confirmarClave = it },
                label = { Text("Confirmar Contraseña") },
                leadingIcon = { Icon(Icons.Filled.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.actualizarClave(nuevaClave)
                    onBack()
                },
                enabled = nuevaClave.length >= 4 && nuevaClave == confirmarClave,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Cambiar Contraseña", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
