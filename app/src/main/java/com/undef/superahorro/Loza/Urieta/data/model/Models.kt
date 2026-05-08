package com.undef.superahorro.Loza.Urieta.data.model

/**
 * Modelos de dominio de la app.
 *
 * Son data class de Kotlin: el compilador genera automáticamente equals(),
 * hashCode(), toString() y copy(). El método copy(...) es el que usamos
 * en MockData.actualizarCompra para crear una compra modificada manteniendo
 * los demás campos sin tocar.
 *
 * Para la 1ra entrega son data class simples; en la 2da entrega se mapearán
 * a entidades Room agregándoles las anotaciones @Entity, @PrimaryKey, etc.
 * El cambio será mínimo y compatible con todo el código que ya las usa.
 */

/** Usuario logueado. avatarUrl es nullable porque puede no tener foto. */
data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val avatarUrl: String? = null
)

/**
 * Una compra de supermercado. La total es lo que pagó el usuario en el ticket
 * y NO se calcula a partir de los productos: ambos son independientes
 * (puede haber descuentos, productos no detallados, etc.).
 */
data class Compra(
    val id: Int,
    val fecha: String,            // formato yyyy-MM-dd
    val hora: String,             // formato HH:mm
    val supermercado: String,
    val total: Double,
    val ticketImagenUri: String? = null,
    val productos: List<Producto> = emptyList()
)

/**
 * Producto comprado dentro de una compra. El subtotal se calcula en tiempo
 * de lectura (computed property) — no hace falta guardarlo, evitando que
 * quede desactualizado si se cambia la cantidad o el precio.
 */
data class Producto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val cantidad: Int,
    val precio: Double
) {
    /** Computed property: cantidad × precio, calculado al leer. */
    val subtotal: Double get() = cantidad * precio
}
