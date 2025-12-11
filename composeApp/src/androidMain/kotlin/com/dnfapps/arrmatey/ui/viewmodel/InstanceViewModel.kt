package com.dnfapps.arrmatey.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.database.InstanceRepository
import com.dnfapps.arrmatey.database.dao.InstanceDao
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InstanceViewModel: ViewModel(), KoinComponent {

    private val repository: InstanceRepository by inject()

    val allInstances: StateFlow<List<Instance>> = repository.allInstances
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun safeGet(type: InstanceType): Instance {
        return allInstances.value.first { it.type == type }
    }

    fun newInstance(instance: Instance) {
        viewModelScope.launch {
            repository.newInstance(instance)
        }
    }

    fun getFirstInstance(type: InstanceType) = repository.getFirstInstance(type)

}