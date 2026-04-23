package com.fittrack.gym.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.gym.data.entity.Actividad
import com.fittrack.gym.domain.ActividadRepository
import kotlinx.coroutines.launch

class AdminActividadesViewModel(
    private val actividadRepo: ActividadRepository
) : ViewModel() {

    private val _actividades = MutableLiveData<List<Actividad>>()
    val actividades: LiveData<List<Actividad>> = _actividades

    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> = _mensaje

    fun cargarActividades() {
        viewModelScope.launch {
            val lista = actividadRepo.getActividadesCalendario()
            _actividades.value = lista
        }
    }

    fun guardar(actividad: Actividad) {
        viewModelScope.launch {
            if (actividad.id == 0) {
                actividadRepo.crear(actividad)
            } else {
                actividadRepo.editar(actividad)
            }
            cargarActividades()
        }
    }

    fun onBorrarActividad(actividad: Actividad) {
        viewModelScope.launch {
            actividadRepo.borrar(actividad)
            _mensaje.value = "Actividad '${actividad.nombre}' eliminada"
            cargarActividades()
        }
    }

    fun onMensajeMostrado() {
        _mensaje.value = null
    }
}