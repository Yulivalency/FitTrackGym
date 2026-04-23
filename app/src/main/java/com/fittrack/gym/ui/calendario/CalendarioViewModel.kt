package com.fittrack.gym.ui.calendario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.gym.domain.ActividadRepository
import com.fittrack.gym.domain.ReservaRepository
import kotlinx.coroutines.launch

class CalendarioViewModel(
    private val actividadRepository: ActividadRepository,
    private val reservaRepository: ReservaRepository,
    private val usuarioId: Int

) : ViewModel() {

    // Lista de items para el RecyclerView
    private val _items = MutableLiveData<List<ActividadCalendarioItem>>()
    val items: LiveData<List<ActividadCalendarioItem>> = _items

    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> = _mensaje

    // NUEVO: recordamos último día seleccionado
    private var diaSeleccionado: DiaCalendarioUi? = null

    /**
     * Carga las actividades del día indicado por diaUi.diaSemanaInt (1..7)
     */
    fun cargarCalendario(diaUi: DiaCalendarioUi) {
        diaSeleccionado = diaUi

        viewModelScope.launch {
            // 1) Traemos TODAS las actividades ordenadas por día y hora
            val actividadesSemana = actividadRepository.listarSemana()

            // 2) Filtramos solo las del día seleccionado
            val actividadesDelDia = actividadesSemana.filter {
                it.diaSemana == diaUi.diaSemanaInt
            }

            // 3) Mapear a ActividadCalendarioItem (igual que ya hacías)
            val listaUi = actividadesDelDia.map { actividad ->
                val reserva = reservaRepository.getReservaUsuario(usuarioId, actividad.id)

                val estadoUi = when (reserva?.estado) {
                    "RESERVADA" -> EstadoReservaUi(TipoReservaUi.RESERVADA)
                    "EN_ESPERA" -> EstadoReservaUi(TipoReservaUi.EN_ESPERA, reserva.posLista)
                    else        -> EstadoReservaUi(TipoReservaUi.NINGUNA)
                }

                val textoBoton = when (estadoUi.tipo) {
                    TipoReservaUi.NINGUNA   -> "Reservar"
                    TipoReservaUi.RESERVADA -> "Anular"
                    TipoReservaUi.EN_ESPERA -> "Salir de lista"
                }

                val badgeTexto = when (estadoUi.tipo) {
                    TipoReservaUi.RESERVADA -> "Reservada"
                    TipoReservaUi.EN_ESPERA -> "En espera"
                    TipoReservaUi.NINGUNA   -> "Disponible"
                }

                ActividadCalendarioItem(
                    actividad = actividad,
                    estadoReservaUi = estadoUi,
                    textoBoton = textoBoton,
                    badgeTexto = badgeTexto,
                )
            }

            _items.value = listaUi
        }
    }

    fun onToggleReserva(actividadId: Int) {
        viewModelScope.launch {
            val ahora = System.currentTimeMillis()
            // Solo hacemos el toggle en BD

            // 1) Miramos el estado actual del item en la lista
            val listaActual = _items.value ?: emptyList()
            val itemActual = listaActual.find { it.actividad.id == actividadId }
            val estadoAntes = itemActual?.estadoReservaUi?.tipo ?: TipoReservaUi.NINGUNA

            // 2) Llamamos al repositorio (hace la magia en BD)
            val res = reservaRepository.toggleReserva(usuarioId, actividadId, ahora)

            // 3) Decidimos el mensaje según LO QUE HABÍA ANTES + el resultado
            val msg = when (estadoAntes) {
                TipoReservaUi.NINGUNA -> {
                    // Antes no tenía nada → ahora ha reservado
                    if (res.startsWith("EN_ESPERA")) {
                        "Clase completa. Has quedado en lista de espera."
                    } else {
                        "¡Reserva confirmada!"
                    }
                }
                TipoReservaUi.RESERVADA -> {
                    // Antes tenía reserva firme → ahora la ha anulado
                    "Reserva anulada."
                }
                TipoReservaUi.EN_ESPERA -> {
                    // Antes estaba en lista → ahora ha salido de la lista
                    "Has salido de la lista de espera."
                }
            }

            _mensaje.value = msg
            // Y recargamos el calendario para refrescar botones/badges
            diaSeleccionado?.let { cargarCalendario(it) }
        }
    }

    fun onMensajeMostrado() {
        _mensaje.value = null
    }
}