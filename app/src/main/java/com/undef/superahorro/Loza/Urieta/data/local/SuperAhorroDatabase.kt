package com.undef.superahorro.Loza.Urieta.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.undef.superahorro.Loza.Urieta.data.model.Compra
import com.undef.superahorro.Loza.Urieta.data.model.Producto

@Database(entities = [Compra::class, Producto::class], version = 1, exportSchema = false)
abstract class SuperAhorroDatabase : RoomDatabase() {

    abstract fun compraDao(): CompraDao

    companion object {
        @Volatile
        private var INSTANCE: SuperAhorroDatabase? = null

        fun getDatabase(context: Context): SuperAhorroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SuperAhorroDatabase::class.java,
                    "super_ahorro_database"
                )
                .fallbackToDestructiveMigration() // Útil durante el desarrollo
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
