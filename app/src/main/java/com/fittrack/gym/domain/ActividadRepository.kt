package com.fittrack.gym.domain

import com.fittrack.gym.data.AppDatabase
import com.fittrack.gym.data.dao.ActividadDao
import com.fittrack.gym.data.entity.Actividad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ActividadRepository(
    private val db: AppDatabase,
    private val actividadDao: ActividadDao = db.actividadDao()
) {
    suspend fun listarSemana(): List<Actividad> =
        withContext(Dispatchers.IO) { actividadDao.listarSemana() }

    suspend fun crear(a: Actividad): Long =
        withContext(Dispatchers.IO) { actividadDao.insert(a) }

    suspend fun editar(a: Actividad) =
        withContext(Dispatchers.IO) { actividadDao.update(a) }

    suspend fun borrar(a: Actividad) =
        withContext(Dispatchers.IO) { actividadDao.delete(a) }

    /**
     * Devuelve la lista de actividades para el calendario.
     * La UI usará siempre este método.
     */
    suspend fun getActividadesCalendario(): List<Actividad> =
        withContext(Dispatchers.IO) {
            actividadDao.getAllActividades()
        }

    suspend fun tieneActividades(): Boolean =
        withContext(Dispatchers.IO) {
            actividadDao.countActividades() > 0
        }

    suspend fun getById(id: Int): Actividad? =
        withContext(Dispatchers.IO) {
            actividadDao.getById(id)
        }
}