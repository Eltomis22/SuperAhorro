package com.undef.superahorro.Loza.Urieta.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.data.model.Producto
import com.undef.superahorro.Loza.Urieta.data.model.UserEntity

@Database(
    entities = [Compra::class, Producto::class, UserEntity::class], 
    version = 2, // Subimos versión por la nueva tabla de usuarios
    exportSchema = false
)
abstract class SuperAhorroDatabase : RoomDatabase() {

    abstract fun compraDao(): CompraDao
    abstract fun productoDao(): ProductoDao
    abstract fun userDao(): UserDao

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
