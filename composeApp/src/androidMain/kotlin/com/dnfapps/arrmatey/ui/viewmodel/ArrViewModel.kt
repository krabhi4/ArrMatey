package com.dnfapps.arrmatey.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.viewmodel.BaseArrRepository
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

abstract class ArrViewModel(protected val instance: Instance): ViewModel(), KoinComponent {
    protected val repository: BaseArrRepository<out AnyArrMedia> by inject {
        parametersOf(
            instance
        )
    }

    val uiState = repository.uiState
    val detailsUiState = repository.detailUiState

    fun refreshLibrary() {
        viewModelScope.launch {
            repository.refreshLibrary()
        }
    }

    open fun getDetails(id: Int) {
        viewModelScope.launch {
            repository.getDetails(id)
        }
    }

    fun setMonitorStatus(id: Int, monitorStatus: Boolean) {
        viewModelScope.launch {
            repository.setMonitorStatus(id, monitorStatus)
        }
    }
}

class ArrViewModelFactory(
    private val instance: Instance
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArrViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return when (instance.type) {
                InstanceType.Sonarr -> SonarrViewModel(instance) as T
                InstanceType.Radarr -> RadarrViewModel(instance) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}