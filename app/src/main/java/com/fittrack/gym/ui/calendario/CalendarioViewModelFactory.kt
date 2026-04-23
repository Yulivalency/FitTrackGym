package com.fittrack.gym.ui.calendario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fittrack.gym.domain.ActividadRepository
import com.fittrack.gym.domain.ReservaRepository

class CalendarioViewModelFactory(
    private val actividadRepository: ActividadRepository,
    private val reservaRepository: ReservaRepository,
    private val usuarioId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarioViewModel::class.java)) {
            return CalendarioViewModel(
                actividadRepository,
                reservaRepository,
                usuarioId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}