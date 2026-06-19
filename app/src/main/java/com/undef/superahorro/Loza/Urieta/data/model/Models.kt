package com.undef.superahorro.Loza.Urieta.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Modelos de dominio de la app, ahora convertidos en Entidades de Room.
 */

/** Usuario logueado (Domain model) */
data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val avatarUrl: String? = null
)

/** Entidad de Usuario para Room (Persistence model) */
@Entity(tableName = "usuarios")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val clave: String // En una app real esto debería estar hasheado
)

/**
 * Una compra de supermercado.
 */
@Entity(tableName = "compras")
data class Compra(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fecha: String,            // formato yyyy-MM-dd
    val hora: String,             // formato HH:mm
    val supermercado: String,
    val total: Double,
    val ticketImagenUri: String? = null,
    
    @Ignore
    val productos: List<Producto> = emptyList() // Temporal para compatibilidad con MockData
) {
    // Constructor secundario para Room (ya que ignora 'productos')
    constructor(id: Int, fecha: String, hora: String, supermercado: String, total: Double, ticketImagenUri: String?) : 
        this(id, fecha, hora, supermercado, total, ticketImagenUri, emptyList())
}

/**
 * Producto comprado dentro de una compra.
 */
@Entity(
    tableName = "productos",
    foreignKeys = [
        ForeignKey(
            entity = Compra::class,
            parentColumns = ["id"],
            childColumns = ["compraId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["compraId"])]
)
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val compraId: Int = 0, // Relación con la compra (default 0 para MockData temporal)
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val cantidad: Int,
    val precio: Double
) {
    /** Computed property: cantidad × precio, calculado al leer. */
    val subtotal: Double get() = cantidad * precio
}

/**
 * Clase de soporte para obtener una compra con todos sus productos
 * (Relación 1 a muchos).
 */
data class CompraConProductos(
    @Embedded val compra: Compra,
    @Relation(
        parentColumn = "id",
        entityColumn = "compraId"
    )
    val productos: List<Producto>
)
