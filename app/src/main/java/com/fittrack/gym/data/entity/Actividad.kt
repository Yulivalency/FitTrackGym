package com.fittrack.gym.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividad")
data class Actividad(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val diaSemana: Int,     // 1=Lun … 7=Dom
    val horaInicio: String, // "18:00"
    val horaFin: String,    // "19:00"
    val aforo: Int
): java.io.Serializable