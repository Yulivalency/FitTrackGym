package com.fittrack.gym.ui.misreservas

import androidx.lifecycle.*
import com.fittrack.gym.domain.ActividadRepository
import com.fittrack.gym.domain.ReservaRepository
import kotlinx.coroutines.launch

class MisReservasViewModel(
    private val actividadRepo: ActividadRepository,
    private val reservaRepo: ReservaRepository,
    private val usuarioId: Int
) : ViewModel() {

    private val _items = MutableLiveData<List<ReservaUiItem>>()
    val items: LiveData<List<ReservaUiItem>> = _items

    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> = _mensaje

    fun cargarReservas() {
        viewModelScope.launch {
            // 1) Traer las reservas de ese usuario
            val reservas = reservaRepo.getReservasUsuario(usuarioId)

            // 2) Para cada reserva, buscamos su actividad
            val lista = reservas.mapNotNull { r ->
                val act = actividadRepo.getById(r.actividadId) ?: return@mapNotNull null

                val textoBoton = when (r.estado) {
                    "RESERVADA" -> "Anular"
                    "EN_ESPERA" -> "Salir de lista"
                    else -> ""
                }

                val badge = when (r.estado) {
                    "RESERVADA" -> "Reservada"
                    "EN_ESPERA" -> "En espera (${r.posLista ?: "-"})"
                    else -> ""
                }

                ReservaUiItem(
                    actividad = act,
                    reserva = r,
                    textoBoton = textoBoton,
                    badgeTexto = badge
                )
            }

            _items.value = lista
        }
    }

    fun onBotonReserva(reservaId: Int) {
        viewModelScope.launch {
            val res = reservaRepo.cancelar(reservaId)
            val msg = when (res) {
                "CANCELADA_CON_PROMOCION" -> "Has anulado tu reserva. Se ha promovido a otra persona de la lista."
                "CANCELADA_SIN_ESPERA" -> "Has anulado tu reserva."
                "ESPERA_CANCELADA" -> "Has salido de la lista de espera."
                "YA_CANCELADA_O_DESCONOCIDA" -> "La reserva ya estaba cancelada."
                "NO_ENCONTRADA" -> "No se ha encontrado la reserva."
                else -> "Operación realizada."
            }

            _mensaje.value =  if (msg.isNotEmpty()) msg else null
            cargarReservas()
        }
    }

    fun onMensajeMostrado() {
        _mensaje.value = null
    }
}