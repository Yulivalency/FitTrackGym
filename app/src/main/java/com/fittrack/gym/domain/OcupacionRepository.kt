package com.fittrack.gym.domain

import com.fittrack.gym.data.AppDatabase
import com.fittrack.gym.data.dao.ActividadDao
import com.fittrack.gym.data.dao.ReservaDao
import com.fittrack.gym.data.entity.Actividad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class OcupacionDato(
    val actividad: Actividad,
    val plazasOcupadas: Int
)

class OcupacionRepository(
    private val db: AppDatabase,
    private val actividadDao: ActividadDao = db.actividadDao(),
    private val reservaDao: ReservaDao = db.reservaDao()
) {

    /** Ocupación de todas las actividades (para la pantalla). */
    suspend fun ocupacionSemanal(): List<OcupacionDato> =
        withContext(Dispatchers.IO) {
            val actividades = actividadDao.getAllActividades()
            actividades.map { act ->
                val firmes = reservaDao.countFirmes(act.id)
                OcupacionDato(actividad = act, plazasOcupadas = firmes)
            }
        }

    /** Nº de reservas firmes para una actividad concreta */
    suspend fun ocupacionPorActividad(actividadId: Int): Int =
        withContext(Dispatchers.IO) {
            reservaDao.countFirmes(actividadId)
        }
}
