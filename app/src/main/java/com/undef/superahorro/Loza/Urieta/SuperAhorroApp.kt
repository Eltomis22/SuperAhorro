package com.undef.superahorro.Loza.Urieta

import android.app.Application
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.local.SuperAhorroDatabase

/**
 * Clase Application para inicializar la base de datos y el repositorio
 * de forma global (Singleton manual).
 */
class SuperAhorroApp : Application() {

    private val database by lazy { SuperAhorroDatabase.getDatabase(this) }
    val repository by lazy { SuperAhorroRepository(database.compraDao()) }

    override fun onCreate() {
        super.onCreate()
    }
}
