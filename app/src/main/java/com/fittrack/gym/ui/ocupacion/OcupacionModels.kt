package com.fittrack.gym.ui.ocupacion

import com.fittrack.gym.data.entity.Actividad

data class OcupacionUiItem(
    val actividad: Actividad,
    val plazasOcupadas: Int,
    val porcentaje: Int
)
