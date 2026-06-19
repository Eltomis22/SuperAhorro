package com.undef.superahorro.Loza.Urieta.data.local

import androidx.room.*
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos WHERE compraId = :compraId")
    fun obtenerProductosDeCompra(compraId: Int): Flow<List<Producto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: Producto): Long

    @Delete
    suspend fun eliminarProducto(producto: Producto)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun eliminarProductoPorId(id: Int)

    @Query("SELECT * FROM productos")
    suspend fun obtenerTodosLosProductosSnapshot(): List<Producto>
}
