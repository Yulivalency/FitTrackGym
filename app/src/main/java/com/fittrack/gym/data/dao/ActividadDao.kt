package com.fittrack.gym.data.dao

import androidx.room.*
import com.fittrack.gym.data.entity.Actividad

// --- NUEVO: POJO para la lista con contadores ---
data class ActividadStats(
    val id: Int,
    val nombre: String,
    val diaSemana: Int,
    val horaInicio: String,
    val horaFin: String,
    val aforo: Int,
    val ocupadas: Int,  //RESERVADA
    val enEspera: Int   //EN_ESPERA
)

@Dao
interface ActividadDao {
    @Insert
    suspend fun insert(a: Actividad): Long

    @Update
    suspend fun update(a: Actividad)

    @Delete
    suspend fun delete(a: Actividad)

    @Query("SELECT * FROM actividad ORDER BY diaSemana, horaInicio")
    suspend fun listarSemana(): List<Actividad>

    @Query("""
    SELECT COUNT(*) FROM reserva 
    WHERE actividadId = :actividadId AND estado = 'RESERVADA'
""")
    suspend fun countReservadas(actividadId: Int): Int

    @Query("""
    SELECT COUNT(*) FROM reserva 
    WHERE actividadId = :actividadId AND estado = 'EN_ESPERA'
""")
    suspend fun countEspera(actividadId: Int): Int

    @Query("""
        SELECT 
          a.id, a.nombre, a.diaSemana, a.horaInicio, a.horaFin, a.aforo,
          (SELECT COUNT(*) FROM reserva r  WHERE r.actividadId = a.id AND r.estado='RESERVADA') AS ocupadas,
          (SELECT COUNT(*) FROM reserva r2 WHERE r2.actividadId = a.id AND r2.estado='EN_ESPERA') AS enEspera
        FROM actividad a
        ORDER BY a.diaSemana, a.horaInicio
    """)
    suspend fun listarSemanaStats(): List<ActividadStats>

    @Query("SELECT * FROM actividad")
    suspend fun getAllActividades(): List<Actividad>

    @Query("SELECT * FROM actividad WHERE id = :id")
    suspend fun getById(id: Int): Actividad?

    @Query("SELECT COUNT(*) FROM actividad")
    suspend fun countActividades(): Int

}