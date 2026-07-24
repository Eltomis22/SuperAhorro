package com.undef.superahorro.Loza.Urieta.data.local

import androidx.room.*
import com.undef.superahorro.Loza.Urieta.data.model.PresupuestoEntity

@Dao
interface PresupuestoDao {

    @Query("SELECT * FROM presupuestos WHERE usuarioEmail = :email")
    suspend fun obtenerPorUsuario(email: String): List<PresupuestoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(presupuestos: List<PresupuestoEntity>)

    @Query("DELETE FROM presupuestos WHERE usuarioEmail = :email")
    suspend fun eliminarPorUsuario(email: String)
}
