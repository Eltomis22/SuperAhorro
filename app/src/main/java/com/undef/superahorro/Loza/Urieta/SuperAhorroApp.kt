package com.undef.superahorro.Loza.Urieta

import android.app.Application
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.local.SuperAhorroDatabase
import com.undef.superahorro.Loza.Urieta.data.remote.SuperAhorroApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Clase Application para inicializar la base de datos, el repositorio
 * y el cliente de red de forma global (Singleton manual).
 */
class SuperAhorroApp : Application() {

    // 1. Inicialización de la Base de Datos Local
    private val database by lazy { SuperAhorroDatabase.getDatabase(this) }

    // 2. Inicialización de Retrofit para Networking
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SuperAhorroApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api by lazy { retrofit.create(SuperAhorroApi::class.java) }

    // 3. Inicialización del Repositorio (Inyectando DAO y API)
    val repository by lazy { 
        SuperAhorroRepository(
            compraDao = database.compraDao(),
            api = api
        ) 
    }

    override fun onCreate() {
        super.onCreate()
    }
}
