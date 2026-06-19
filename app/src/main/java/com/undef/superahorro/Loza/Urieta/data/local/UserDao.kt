package com.undef.superahorro.Loza.Urieta.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.undef.superahorro.Loza.Urieta.data.model.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerUsuarioPorEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UserEntity)

    @Query("UPDATE usuarios SET nombre = :nombre WHERE email = :email")
    suspend fun actualizarNombre(email: String, nombre: String)

    @Query("UPDATE usuarios SET clave = :nuevaClave WHERE email = :email")
    suspend fun actualizarClave(email: String, nuevaClave: String)

    @Query("UPDATE usuarios SET email = :nuevoEmail WHERE email = :viejoEmail")
    suspend fun actualizarEmail(viejoEmail: String, nuevoEmail: String)
}
