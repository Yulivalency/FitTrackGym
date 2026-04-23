package com.fittrack.gym.ui.calendario

import com.fittrack.gym.data.entity.Actividad

// Tipo de estado para la UI
enum class TipoReservaUi {
    NINGUNA,
    RESERVADA,
    EN_ESPERA
}
// Estado que pinta la interfaz
data class EstadoReservaUi(
    val tipo: TipoReservaUi,
    val posLista: Int? = null
)

/**
 * Lo que el RecyclerView necesita por cada actividad.
 * Usamos directamente la entidad Actividad para no inventar campos.
 */
data class ActividadCalendarioItem(
    val actividad: Actividad,
    val estadoReservaUi: EstadoReservaUi,
    val textoBoton: String,   // "Reservar" / "Anular" / "Salir de lista" "
    val badgeTexto: String,   // "Disponible" / "Completa" / "Reservada" / "En espera"
)

data class DiaCalendarioUi(
    val diaSemanaInt: Int,      // 1 = Lunes … 7 = Domingo (como Actividad.diasemana)
    val diaSemanaCorto: String, // "Lun"
    val numero: String          // "28" (día del mes para mostrar)
)