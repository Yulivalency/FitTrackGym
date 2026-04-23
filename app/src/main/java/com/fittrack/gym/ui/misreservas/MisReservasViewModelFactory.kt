package com.fittrack.gym.ui.misreservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fittrack.gym.domain.ActividadRepository
import com.fittrack.gym.domain.ReservaRepository

class MisReservasViewModelFactory(
    private val actividadRepo: ActividadRepository,
    private val reservaRepo: ReservaRepository,
    private val usuarioId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MisReservasViewModel::class.java)) {
            return MisReservasViewModel(actividadRepo, reservaRepo, usuarioId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}