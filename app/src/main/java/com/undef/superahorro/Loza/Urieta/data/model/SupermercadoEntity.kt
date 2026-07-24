package com.undef.superahorro.Loza.Urieta.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad para cachear los supermercados sugeridos desde la API.
 */
@Entity(tableName = "supermercados_cache")
data class SupermercadoEntity(
    @PrimaryKey
    val nombre: String
)
