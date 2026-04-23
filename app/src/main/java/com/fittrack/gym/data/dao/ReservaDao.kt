package com.fittrack.gym.data.dao

import androidx.room.*
import com.fittrack.gym.data.entity.Reserva

@Dao
interface ReservaDao {
    @Insert
    suspend fun insert(r: Reserva): Long

    @Update
    suspend fun update(r: Reserva)

    @Query("SELECT * FROM reserva WHERE id = :id")
    suspend fun getById(id: Int): Reserva?

    // nº de reservas firmes para una actividad
    @Query("SELECT COUNT(*) FROM reserva WHERE actividadId = :actividadId AND estado = 'RESERVADA'")
    suspend fun countFirmes(actividadId: Int): Int

    // última posición de la lista de espera
    @Query("SELECT COALESCE(MAX(posLista), 0) FROM reserva WHERE actividadId = :actividadId AND estado = 'EN_ESPERA'")
    suspend fun maxPosLista(actividadId: Int): Int

    // primero de la lista (posLista = 1)
    @Query("SELECT * FROM reserva WHERE actividadId = :actividadId AND estado = 'EN_ESPERA' AND posLista = 1 LIMIT 1")
    suspend fun topEspera(actividadId: Int): Reserva?

    @Query("SELECT COUNT(*) FROM Actividad")
    suspend fun countActividades(): Int

    // reindexar posiciones > 1 tras promover
    @Query("""
        UPDATE reserva
        SET posLista = posLista - 1
        WHERE actividadId = :actividadId AND estado = 'EN_ESPERA' AND posLista > 1
    """)
    suspend fun shiftEspera(actividadId: Int)

    // reservas de un usuario (para "Mis Reservas")
    @Query("""
    SELECT * FROM reserva
    WHERE usuarioId = :usuarioId
      AND estado IN ('RESERVADA', 'EN_ESPERA')
    ORDER BY fechaHoraReserva DESC
""")
    suspend fun reservasUsuario(usuarioId: Int): List<Reserva>

    // ¿Tiene este usuario una reserva firme en esa actividad?
    @Query("""
    SELECT * FROM reserva 
    WHERE usuarioId = :uId AND actividadId = :actId AND estado='RESERVADA'
    LIMIT 1
""")
    suspend fun findFirme(uId: Int, actId: Int): Reserva?

    // ¿Está en lista de espera?
    @Query("""
    SELECT * FROM reserva 
    WHERE usuarioId = :uId AND actividadId = :actId AND estado='EN_ESPERA'
    LIMIT 1
""")
    suspend fun findEspera(uId: Int, actId: Int): Reserva?

    // Marcar cancelada (si quieres conservar histórico)
    @Query("UPDATE reserva SET estado='CANCELADA', posLista=NULL WHERE id=:id")
    suspend fun marcarCancelada(id: Int)

    // Promover a plaza firme
    @Query("UPDATE reserva SET estado='RESERVADA', posLista=NULL WHERE id=:id")
    suspend fun promoverAFirme(id: Int)

    // Compactar la lista desde una posición concreta (cuando borras p.e. pos 3)
    @Query("""
    UPDATE reserva
    SET posLista = posLista - 1
    WHERE actividadId = :actId AND estado='EN_ESPERA' AND posLista > :desde
""")
    suspend fun compactarDesde(actId: Int, desde: Int)

    // Eliminar (si prefieres borrar en vez de marcar cancelada la espera)
    @Query("DELETE FROM reserva WHERE id=:id")
    suspend fun borrar(id: Int)

    @Query("""
    SELECT * FROM reserva
    WHERE usuarioId = :uId
      AND actividadId = :actId
      AND estado IN ('RESERVADA', 'EN_ESPERA')
      ORDER BY fechaHoraReserva DESC
    LIMIT 1
""")
    suspend fun reservaDe(uId: Int, actId: Int): Reserva?
}