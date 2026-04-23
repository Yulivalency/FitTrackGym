package com.fittrack.gym.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fittrack.gym.domain.ActividadRepository

class AdminActividadesViewModelFactory(
    private val actividadRepo: ActividadRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminActividadesViewModel(actividadRepo) as T
    }
}
