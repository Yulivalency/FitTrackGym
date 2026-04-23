package com.fittrack.gym.ui.misreservas

import com.fittrack.gym.data.entity.Actividad
import com.fittrack.gym.data.entity.Reserva

data class ReservaUiItem(
    val actividad: Actividad,
    val reserva: Reserva,
    val textoBoton: String,
    val badgeTexto: String
)

