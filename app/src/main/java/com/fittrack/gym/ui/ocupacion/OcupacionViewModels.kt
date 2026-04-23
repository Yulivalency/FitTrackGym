package com.fittrack.gym.ui.ocupacion

import androidx.lifecycle.*
import com.fittrack.gym.domain.OcupacionRepository
import kotlinx.coroutines.launch

class OcupacionViewModel(
    private val ocupacionRepo: OcupacionRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<OcupacionUiItem>>()
    val items: LiveData<List<OcupacionUiItem>> = _items

    fun cargarOcupacion() {
        viewModelScope.launch {
            val datos = ocupacionRepo.ocupacionSemanal()

            val lista = datos.map { d ->
                val aforo = d.actividad.aforo
                val porcentaje =
                    if (aforo > 0) (d.plazasOcupadas * 100 / aforo) else 0

                OcupacionUiItem(
                    actividad = d.actividad,
                    plazasOcupadas = d.plazasOcupadas,
                    porcentaje = porcentaje
                )
            }.sortedWith(
                compareBy({ it.actividad.diaSemana }, { it.actividad.horaInicio })
            )

            _items.value = lista
        }
    }
}