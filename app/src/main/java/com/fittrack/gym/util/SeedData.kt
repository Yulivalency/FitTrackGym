package com.fittrack.gym.util

import com.fittrack.gym.data.AppDatabase
import com.fittrack.gym.data.entity.Actividad
import com.fittrack.gym.data.entity.Reserva
import com.fittrack.gym.data.entity.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SeedData {
    suspend fun run(db: AppDatabase) = withContext(Dispatchers.IO) {
        // Usuarios demo
        // 1) Limpiar tablas
        db.clearAllTables()

        val actividadDao = db.actividadDao()
        val usuarioDao = db.usuarioDao()
        val reservaDao = db.reservaDao()

        // 2) Usuarios demo
        val usuarios = listOf(
            Usuario(id = 1, nombre = "Admin", email = "admin@gym.com", rol = "ADMIN"),
            Usuario(id = 2, nombre = "Cliente Demo", email = "demo@gym.com", rol = "CLIENTE"),
            Usuario(id = 3, nombre = "Usuario 3", email = "u3@gym.com", rol = "CLIENTE"),
            Usuario(id = 4, nombre = "Usuario 4", email = "u4@gym.com", rol = "CLIENTE"),
            Usuario(id = 5, nombre = "Usuario 5", email = "u5@gym.com", rol = "CLIENTE"),
            Usuario(id = 6, nombre = "Usuario 6", email = "u6@gym.com", rol = "CLIENTE"),
            Usuario(id = 7, nombre = "Usuario 7", email = "u7@gym.com", rol = "CLIENTE"),
            Usuario(id = 8, nombre = "Usuario 8", email = "u8@gym.com", rol = "CLIENTE")
        )
        usuarios.forEach { usuarioDao.insert(it) }

        // 3) Actividades (lunes–sábado)
        val actividades = listOf(
                Actividad(
                    id= 0,
                    nombre = "Live BodyPump",
                    diaSemana = 1,               // Lunes
                    horaInicio = "10:00",
                    horaFin = "11:00",
                    aforo = 5
                ),
                Actividad(
                    id= 0,
                    nombre = "Live BodyCombat",
                    diaSemana = 2,                 // Martes
                    horaInicio = "12:00",
                    horaFin = "13:00",
                    aforo = 20
                ),

                Actividad(
                    id= 0,
                    nombre = "Live Zumba",
                    diaSemana = 3,           // Miércoles
                    horaInicio = "18:00",
                    horaFin = "19:00",
                    aforo = 25
                ),

                Actividad(
                    id= 0,
                    nombre = "Live Pilates",
                    diaSemana = 4,             // Jueves
                    horaInicio = "19:00",
                    horaFin = "20:00",
                    aforo = 15
                ),

                Actividad(
                    id= 0,
                    nombre = "Live Booty & Abs",
                    diaSemana = 5,                 // Viernes
                    horaInicio = "10:00",
                    horaFin = "11:00",
                    aforo = 15
                ),


            Actividad(
                id = 0,
                nombre = "Spinning",
                diaSemana = 6,              // Sábado
                horaInicio = "10:00",
                horaFin = "11:00",
                aforo = 8
            ),


                Actividad(
                    id= 0,
                    nombre = "Yoga",
                    diaSemana = 6,           // Sábado
                    horaInicio = "11:30",
                    horaFin = "12:30",
                    aforo = 10
                )
            )
        // Insertar actividades y recuperar sus IDs autogenerados
        val idsActividades = actividades.map { actividadDao.insert(it).toInt() }

        val idBodyPump   = idsActividades[0]
        val idBodyCombat = idsActividades[1]
        val idZumba      = idsActividades[2]
        val idPilates    = idsActividades[3]
        val idBootyAbs   = idsActividades[4]
        val idSpinning   = idsActividades[5]
        val idYoga       = idsActividades[6]

        val ahora = System.currentTimeMillis()

        // 4) Algunas reservas para simular aforos
        val reservas = mutableListOf<Reserva>()

        // BodyPump: aforo completo (usuarios 3–7), Cliente Demo queda en ESPERA(1) cuando reserve
        reservas += Reserva(0, usuarioId = 3, actividadId = idBodyPump, estado = "RESERVADA", fechaHoraReserva = ahora - 600000, posLista = null)
        reservas += Reserva(0, usuarioId = 4, actividadId = idBodyPump, estado = "RESERVADA", fechaHoraReserva = ahora - 500000, posLista = null)
        reservas += Reserva(0, usuarioId = 5, actividadId = idBodyPump, estado = "RESERVADA", fechaHoraReserva = ahora - 400000, posLista = null)
        reservas += Reserva(0, usuarioId = 6, actividadId = idBodyPump, estado = "RESERVADA", fechaHoraReserva = ahora - 300000, posLista = null)
        reservas += Reserva(0, usuarioId = 7, actividadId = idBodyPump, estado = "RESERVADA", fechaHoraReserva = ahora - 200000, posLista = null)

        // Zumba / Pilates / Booty & Abs / Spinning / Yoga: sin reservas iniciales
        // (así las verás como "Disponible" al entrar)

        reservas.forEach { reservaDao.insert(it) }
    }
}
