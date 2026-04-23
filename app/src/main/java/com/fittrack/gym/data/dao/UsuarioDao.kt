package com.fittrack.gym.data.dao

import androidx.room.*
import com.fittrack.gym.data.entity.Usuario

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insert(u: Usuario): Long

    @Query("SELECT * FROM usuario")
    suspend fun all(): List<Usuario>

    @Query("SELECT * FROM usuario WHERE id = :id")
    suspend fun getById(id: Int): Usuario?
}