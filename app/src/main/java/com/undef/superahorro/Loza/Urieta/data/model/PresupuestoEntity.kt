package com.undef.superahorro.Loza.Urieta.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Entidad para persistir los presupuestos (límites de gasto) localmente.
 */
@Entity(tableName = "presupuestos")
data class PresupuestoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoria: String,
    val montoMaximo: Double,
    val usuarioEmail: String
)
