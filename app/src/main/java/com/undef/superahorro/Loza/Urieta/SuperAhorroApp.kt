package com.undef.superahorro.Loza.Urieta

import android.app.Application
import com.undef.superahorro.Loza.Urieta.data.SuperAhorroRepository
import com.undef.superahorro.Loza.Urieta.data.local.SuperAhorroDatabase
import com.undef.superahorro.Loza.Urieta.data.remote.SuperAhorroApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SuperAhorroApp : Application() {

    private val database by lazy {
        SuperAhorroDatabase.getDatabase(this)
    }

    private val okHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://66632f7a62966e20536deccb.mockapi.io/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api by lazy {
        retrofit.create(SuperAhorroApi::class.java)
    }

    val repository by lazy {
        SuperAhorroRepository(
            compraDao = database.compraDao(),
            productoDao = database.productoDao(),
            userDao = database.userDao(),
            api = api
        )
    }

    override fun onCreate() {
        super.onCreate()
    }
}
