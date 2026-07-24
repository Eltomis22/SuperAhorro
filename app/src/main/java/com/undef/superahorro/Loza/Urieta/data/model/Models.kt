package com.undef.superahorro.Loza.Urieta.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.gson.annotations.SerializedName

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
    @SerializedName("id_local")
    val id: Int = 0,
    
    @SerializedName("fecha")
    val fecha: String? = null,
    
    @SerializedName("hora")
    val hora: String? = null,
    
    @SerializedName("supermercado")
    val supermercado: String? = null,
    
    @SerializedName("total")
    val total: Double = 0.0,
    
    @SerializedName("categoria")
    val categoria: String? = "Otros",
    
    @SerializedName("usuario_email")
    val usuarioEmail: String? = "",
    
    @SerializedName("ticket_imagen_uri")
    val ticketImagenUri: String? = null,
    
    @Ignore
    @SerializedName("productos")
    val productos: List<Producto> = emptyList()
) {
    // Constructor secundario para Room
    constructor(id: Int, fecha: String?, hora: String?, supermercado: String?, total: Double, categoria: String?, usuarioEmail: String?, ticketImagenUri: String?) : 
        this(id, fecha, hora, supermercado, total, categoria, usuarioEmail, ticketImagenUri, emptyList())
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
    val compraId: Int = 0, // Relación con la compra
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

/**
 * Modelo para los mensajes del Chat con IA
 */
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
