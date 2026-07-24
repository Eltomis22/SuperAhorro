package com.undef.superahorro.Loza.Urieta.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import com.undef.superahorro.Loza.Urieta.data.model.UserEntity

import com.undef.superahorro.Loza.Urieta.data.model.SupermercadoEntity

@Database(
    entities = [Compra::class, Producto::class, UserEntity::class, SupermercadoEntity::class], 
    version = 3, // Subimos versión por la tabla de caché
    exportSchema = false
)
abstract class SuperAhorroDatabase : RoomDatabase() {

    abstract fun compraDao(): CompraDao
    abstract fun productoDao(): ProductoDao
    abstract fun userDao(): UserDao
    abstract fun supermercadoDao(): SupermercadoDao

    companion object {
        @Volatile
        private var INSTANCE: SuperAhorroDatabase? = null

        fun getDatabase(context: Context): SuperAhorroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SuperAhorroDatabase::class.java,
                    "superahorro_database"
                )
                .fallbackToDestructiveMigration() // Borra datos viejos al cambiar versión
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
