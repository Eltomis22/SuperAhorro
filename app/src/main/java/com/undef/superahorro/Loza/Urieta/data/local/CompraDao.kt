package com.undef.superahorro.Loza.Urieta.data.local

import androidx.room.*
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.data.model.CompraConProductos
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {

    // --- COMPRAS ---

    @Query("SELECT * FROM compras ORDER BY fecha DESC, hora DESC")
    fun obtenerTodasLasCompras(): Flow<List<Compra>>

    @Query("SELECT * FROM compras")
    suspend fun obtenerTodasLasComprasSnapshot(): List<Compra>

    @Query("SELECT * FROM compras WHERE id = :id")
    suspend fun obtenerCompraPorId(id: Int): Compra?

    @Transaction
    @Query("SELECT * FROM compras WHERE id = :id")
    fun obtenerCompraConProductos(id: Int): Flow<CompraConProductos?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCompra(compra: Compra): Long

    @Update
    suspend fun actualizarCompra(compra: Compra)

    @Delete
    suspend fun eliminarCompra(compra: Compra)

    @Query("DELETE FROM compras WHERE id = :id")
    suspend fun eliminarCompraPorId(id: Int)


    // --- PRODUCTOS ---

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
