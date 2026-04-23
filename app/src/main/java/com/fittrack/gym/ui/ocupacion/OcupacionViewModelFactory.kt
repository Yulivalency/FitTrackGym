package com.fittrack.gym.ui.ocupacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fittrack.gym.domain.OcupacionRepository

class OcupacionViewModelFactory(
    private val ocupacionRepo: OcupacionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OcupacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OcupacionViewModel(ocupacionRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}