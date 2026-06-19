package com.undef.superahorro.Loza.Urieta.data

import androidx.compose.runtime.mutableStateListOf
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import com.undef.superahorro.Loza.Urieta.data.model.User

/**
 * Almacén de datos mockeados para la 1ra entrega.
 */
object MockData {

    val usuarioActual = User(
        id = 1,
        nombre = "Tomas Losa",
        email = "tomas@undef.edu.ar"
    )

    val supermercados = listOf(
        "Carrefour",
        "Coto",
        "Día",
        "Jumbo",
        "La Anónima",
        "Vea",
        "Disco",
        "ChangoMas"
    )

    private val productosCompra1 = listOf(
        Producto(id = 1, compraId = 1, codigo = "7790070410016", nombre = "Leche La Serenísima", descripcion = "Leche entera 1L", cantidad = 2, precio = 950.0),
        Producto(id = 2, compraId = 1, codigo = "7790580121204", nombre = "Pan Lactal Bimbo", descripcion = "Pan lactal blanco 540g", cantidad = 1, precio = 1850.0),
        Producto(id = 3, compraId = 1, codigo = "7790520005014", nombre = "Aceite Natura", descripcion = "Aceite girasol 1.5L", cantidad = 1, precio = 3200.0)
    )

    private val productosCompra2 = listOf(
        Producto(id = 4, compraId = 2, codigo = "7791001000034", nombre = "Yerba Taragüí", descripcion = "Yerba mate 1kg", cantidad = 1, precio = 4500.0),
        Producto(id = 5, compraId = 2, codigo = "7790070401120", nombre = "Queso Cremoso", descripcion = "Queso cremoso 500g", cantidad = 1, precio = 3800.0),
        Producto(id = 6, compraId = 2, codigo = "7791290001008", nombre = "Galletitas Oreo", descripcion = "Galletitas chocolate 118g", cantidad = 3, precio = 1200.0)
    )

    private val productosCompra3 = listOf(
        Producto(id = 7, compraId = 3, codigo = "7790040502154", nombre = "Coca Cola", descripcion = "Gaseosa 2.25L", cantidad = 2, precio = 2100.0),
        Producto(id = 8, compraId = 3, codigo = "7791540067519", nombre = "Detergente Magistral", descripcion = "Detergente 750ml", cantidad = 1, precio = 1950.0)
    )

    val compras = mutableStateListOf(
        Compra(
            id = 1,
            fecha = "2026-04-28",
            hora = "18:30",
            supermercado = "Carrefour",
            total = productosCompra1.sumOf { it.subtotal },
            productos = productosCompra1
        ),
        Compra(
            id = 2,
            fecha = "2026-04-22",
            hora = "11:15",
            supermercado = "Coto",
            total = productosCompra2.sumOf { it.subtotal },
            productos = productosCompra2
        ),
        Compra(
            id = 3,
            fecha = "2026-04-15",
            hora = "20:00",
            supermercado = "Día",
            total = productosCompra3.sumOf { it.subtotal },
            productos = productosCompra3
        ),
        Compra(
            id = 4,
            fecha = "2026-04-08",
            hora = "16:45",
            supermercado = "Jumbo",
            total = 12750.0
        ),
        Compra(
            id = 5,
            fecha = "2026-03-30",
            hora = "19:20",
            supermercado = "Carrefour",
            total = 8430.0
        )
    )

    fun siguienteIdCompra(): Int = (compras.maxOfOrNull { it.id } ?: 0) + 1

    fun siguienteIdProducto(): Int =
        (compras.flatMap { it.productos }.maxOfOrNull { it.id } ?: 0) + 1

    fun agregarCompra(compra: Compra) {
        compras.add(0, compra)
    }

    fun eliminarCompra(compraId: Int) {
        compras.removeAll { it.id == compraId }
    }

    fun actualizarCompra(compraEditada: Compra) {
        val idx = compras.indexOfFirst { it.id == compraEditada.id }
        if (idx >= 0) compras[idx] = compraEditada
    }

    fun eliminarProducto(compraId: Int, productoId: Int) {
        val idx = compras.indexOfFirst { it.id == compraId }
        if (idx < 0) return
        val compra = compras[idx]
        compras[idx] = compra.copy(
            productos = compra.productos.filter { it.id != productoId }
        )
    }

    fun agregarProducto(compraId: Int, producto: Producto) {
        val idx = compras.indexOfFirst { it.id == compraId }
        if (idx < 0) return
        val compra = compras[idx]
        compras[idx] = compra.copy(
            productos = compra.productos + producto
        )
    }

    val gastoMensual = listOf(
        "Nov" to 28500.0,
        "Dic" to 41200.0,
        "Ene" to 35800.0,
        "Feb" to 39400.0,
        "Mar" to 42100.0,
        "Abr" to 47350.0
    )

    val gastoPorSupermercado = listOf(
        "Carrefour" to 22600.0,
        "Coto" to 17500.0,
        "Día" to 8400.0,
        "Jumbo" to 12750.0
    )
}
