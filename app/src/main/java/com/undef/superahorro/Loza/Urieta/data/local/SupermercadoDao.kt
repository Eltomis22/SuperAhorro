package com.undef.superahorro.Loza.Urieta.data.local

import androidx.room.*
import com.undef.superahorro.Loza.Urieta.data.model.SupermercadoEntity

@Dao
interface SupermercadoDao {

    @Query("SELECT nombre FROM supermercados_cache ORDER BY nombre ASC")
    suspend fun obtenerTodos(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(supermercados: List<SupermercadoEntity>)

    @Query("DELETE FROM supermercados_cache")
    suspend fun limpiarCache()
}
