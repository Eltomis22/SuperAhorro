package com.undef.superahorro.Loza.Urieta.data.local

import androidx.room.*
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos WHERE compraId = :compraId")
    fun obtenerProductosDeCompra(compraId: Int): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerProductoPorId(id: Int): Producto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: Producto): Long

    @Delete
    suspend fun eliminarProducto(producto: Producto)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun eliminarProductoPorId(id: Int)

    @Query("""
        SELECT p.* FROM productos p 
        INNER JOIN compras c ON p.compraId = c.id 
        WHERE c.usuarioEmail = :email
    """)
    suspend fun obtenerTodosLosProductosSnapshot(email: String): List<Producto>
}
