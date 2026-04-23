package com.fittrack.gym.domain

import androidx.room.withTransaction
import com.fittrack.gym.data.AppDatabase
import com.fittrack.gym.data.dao.ActividadDao
import com.fittrack.gym.data.dao.ReservaDao
import com.fittrack.gym.data.entity.Reserva
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReservaRepository(
    private val db: AppDatabase,
    private val actividadDao: ActividadDao = db.actividadDao(),
    private val reservaDao: ReservaDao = db.reservaDao()
) {
    /**
     * Intenta reservar. Si el aforo está completo, entra en lista de espera.
     * Devuelve "RESERVADA" o "EN_ESPERA(pos)".
     */
    suspend fun reservar(usuarioId: Int, actividadId: Int, ahoraMillis: Long): String =
        withContext(Dispatchers.IO) {
            db.withTransaction {
                val actividad = actividadDao.getById(actividadId)
                    ?: throw IllegalStateException("Actividad inexistente")

                val firmes = reservaDao.countFirmes(actividadId)

                return@withTransaction if (firmes < actividad.aforo) {
                    // Hay hueco → reserva firme
                    reservaDao.insert(
                        Reserva(
                            usuarioId = usuarioId,
                            actividadId = actividadId,
                            estado = "RESERVADA",
                            fechaHoraReserva = ahoraMillis,
                            posLista = null
                        )
                    )
                    "RESERVADA"
                } else {
                    // No hay hueco → entra en lista de espera
                    val maxPos = reservaDao.maxPosLista(actividadId) ?: 0
                    val nextPos = maxPos + 1

                    reservaDao.insert(
                        Reserva(
                            usuarioId = usuarioId,
                            actividadId = actividadId,
                            estado = "EN_ESPERA",
                            fechaHoraReserva = ahoraMillis,
                            posLista = nextPos
                        )
                    )
                    "EN_ESPERA($nextPos)"
                }
            }
        }

    /**
     * Cancela una reserva por ID.
     * Devuelve:
     *  - "CANCELADA_CON_PROMOCION"
     *  - "CANCELADA_SIN_ESPERA"
     *  - "ESPERA_CANCELADA"
     *  - "YA_CANCELADA_O_DESCONOCIDA"
     *  - "NO_ENCONTRADA"
     */

    suspend fun cancelar(reservaId: Int): String = withContext(Dispatchers.IO) {
        db.withTransaction {
            val r = reservaDao.getById(reservaId)
                ?: return@withTransaction "NO_ENCONTRADA"

            // Si estaba FIRME: se cancela y, si hay espera, promueve al primero
            if (r.estado == "RESERVADA") {
                reservaDao.marcarCancelada(r.id)

                val top = reservaDao.topEspera(r.actividadId)
                if (top != null) {
                    reservaDao.promoverAFirme(top.id)
                    // el que era pos=1 pasa a firme; baja el resto (>1)
                    reservaDao.shiftEspera(r.actividadId)
                    return@withTransaction "CANCELADA_CON_PROMOCION"
                }
                return@withTransaction "CANCELADA_SIN_ESPERA"
            }

            // Si estaba EN_ESPERA: lo quitamos y compactamos desde su posición
            if (r.estado == "EN_ESPERA") {
                val pos = r.posLista ?: 1

                reservaDao.borrar(r.id)
                reservaDao.compactarDesde(r.actividadId, pos)

                return@withTransaction "ESPERA_CANCELADA"
            }
            // Cualquier otro estado
            "YA_CANCELADA_O_DESCONOCIDA"

        }
    }


     /** ÚNICA función que usará la UI.
     *
     * - Si el usuario NO tiene reserva para la actividad → llama a reservar(...)
     * - Si SÍ tiene alguna (firme o en espera) → llama a cancelar(idReserva)
     *
     * Devuelve los mismos códigos que reservar/cancelar:
     *  - "RESERVADA"
     *  - "EN_ESPERA(pos)"
     *  - "CANCELADA_CON_PROMOCION"
     *  - "CANCELADA_SIN_ESPERA"
     *  - "ESPERA_CANCELADA"
     *  - "YA_CANCELADA_O_DESCONOCIDA"
     *  - "NO_ENCONTRADA" (caso raro)*/

    suspend fun toggleReserva(
        usuarioId: Int,
        actividadId: Int,
        ahoraMillis: Long
    ): String = withContext(Dispatchers.IO) {
        db.withTransaction {
            // ¿El usuario ya tiene algo para esta actividad?
            val existente = reservaDao.reservaDe(uId = usuarioId, actId = actividadId)

            if (existente != null) {
                // ---------CASO: YA TIENE RESERVA → TOGGLE = CANCELAR ----------
                return@withTransaction when (existente.estado) {

                    // Tenía una reserva FIRME
                    "RESERVADA" -> {
                        // cancela FIRME y promueve al primero de la espera (si hay)
                        reservaDao.marcarCancelada(existente.id)

                        // Miramos si hay alguien en la lista de espera
                        val top = reservaDao.topEspera(actividadId)
                        if (top != null) {
                            // Promocionamos al primero de la lista a RESERVADA
                            reservaDao.promoverAFirme(top.id)

                            // Reorganizamos la cola de espera (bajan posiciones, etc.)
                            reservaDao.shiftEspera(actividadId)

                            "CANCELADA_CON_PROMOCION"
                        } else {
                            // No había nadie esperando
                            "CANCELADA_SIN_ESPERA"
                        }
                    }

                    // Estaba en LISTA DE ESPERA
                    "EN_ESPERA" -> {
                        // // Guardamos su posición para compactar la cola después
                        val pos = existente.posLista ?: 1


                        // Lo sacamos de la lista
                        reservaDao.borrar(existente.id)

                        // Compactamos desde esa posición (los de detrás pasan una posición hacia delante)
                        reservaDao.compactarDesde(actividadId, pos)
                        "ESPERA_CANCELADA"
                    }

                    else -> {
                        // Ya estaba cancelada u otro estado raro
                        "YA_CANCELADA_O_DESCONOCIDA"
                    }
                }

            } else {
                //  ---------- CASO: NO TIENE NADA → TOGGLE = RESERVAR ----------
                val actividad = actividadDao.getById(actividadId)
                    ?: throw IllegalStateException("Actividad inexistente")

                val firmes = reservaDao.countFirmes(actividadId)

                return@withTransaction if (firmes < actividad.aforo) {
                    // Hay hueco → reserva FIRME
                    reservaDao.insert(
                        Reserva(
                            usuarioId = usuarioId,
                            actividadId = actividadId,
                            estado = "RESERVADA",
                            fechaHoraReserva = ahoraMillis,
                            posLista = null
                        )
                    )
                    "RESERVADA"
                } else {
                    // No hay hueco → entra en LISTA DE ESPERA
                    val maxPos = reservaDao.maxPosLista(actividadId) ?: 0
                    val nextPos = maxPos + 1

                    reservaDao.insert(
                        Reserva(
                            usuarioId = usuarioId,
                            actividadId = actividadId,
                            estado = "EN_ESPERA",
                            fechaHoraReserva = ahoraMillis,
                            posLista = nextPos
                        )
                    )
                    "EN_ESPERA($nextPos)"
                }
            }
        }
    }
    /**
     * Devuelve la reserva del usuario para una actividad (o null si no tiene).
     * Se usará solo para pintar la UI del calendario.
     */
    suspend fun getReservaUsuario(
        usuarioId: Int,
        actividadId: Int
    ): Reserva? = withContext(Dispatchers.IO) {
        reservaDao.reservaDe(uId = usuarioId, actId = actividadId)
    }

    /**
     * Devuelve TODAS las reservas (firme o en espera) del usuario,
     * ordenadas de más reciente a más antigua.
     */
    suspend fun getReservasUsuario(usuarioId: Int): List<Reserva> =
        withContext(Dispatchers.IO) {
            reservaDao.reservasUsuario(usuarioId)
        }
}