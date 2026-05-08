package com.undef.superahorro.Loza.Urieta.data.model

/**
 * Modelos de dominio de la app.
 * Para la 1ra entrega son data class simples; en la 2da se mapearán a entidades Room.
 */

data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val avatarUrl: String? = null
)

data class Compra(
    val id: Int,
    val fecha: String,            // formato yyyy-MM-dd
    val hora: String,             // formato HH:mm
    val supermercado: String,
    val total: Double,
    val ticketImagenUri: String? = null,
    val productos: List<Producto> = emptyList()
)

data class Producto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val cantidad: Int,
    val precio: Double
) {
    val subtotal: Double get() = cantidad * precio
}
