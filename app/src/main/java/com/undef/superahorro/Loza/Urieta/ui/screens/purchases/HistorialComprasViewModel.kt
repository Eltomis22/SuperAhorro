package com.undef.superahorro.Loza.Urieta.ui.screens.purchases

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import kotlinx.coroutines.launch

class HistorialComprasViewModel : ViewModel() {
    private val repository = SuperAhorroRepository()

    // Estado interno mutable para guardar las compras agrupadas
    private val _comprasAgrupadas = mutableStateOf<Map<String, List<Compra>>>(emptyMap())
    // Estado público expuesto a la Screen (Inmutable)
    val comprasAgrupadas: State<Map<String, List<Compra>>> = _comprasAgrupadas

    init {
        cargarCompras()
    }

    private fun cargarCompras() {
        // REQUERIMIENTO DEL PROFESOR: Usamos viewModelScope.launch para manejar la corrutina
        viewModelScope.launch {
            try {
                // Se ejecuta en segundo plano gracias al repositorio suspendido
                val listaCompras = repository.obtenerCompras()

                // Procesamos y agrupamos los datos
                val comprasOrdenadas = listaCompras.sortedByDescending { it.fecha }
                _comprasAgrupadas.value = comprasOrdenadas.groupBy { it.fecha.substring(0, 7) }
            } catch (e: Exception) {
                // Aquí manejarías errores de red o base de datos en la entrega 2
            }
        }
    }
}
