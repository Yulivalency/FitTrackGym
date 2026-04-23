package com.fittrack.gym.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reserva")
data class Reserva(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val actividadId: Int,
    val estado: String,         // "RESERVADA" | "CANCELADA" | "EN_ESPERA"
    val fechaHoraReserva: Long, // epoch millis
    val posLista: Int? = null   // null si no está en lista de espera
)