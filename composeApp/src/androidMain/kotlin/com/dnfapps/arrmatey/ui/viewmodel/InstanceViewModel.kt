package com.dnfapps.arrmatey.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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

    val allInstances = repository.allInstances
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSelected(instance: Instance) {
        viewModelScope.launch {
            repository.setInstanceActive(instance)
        }
    }

    fun delete(instance: Instance) {
        viewModelScope.launch {
            repository.deleteInstance(instance)
        }
    }

}

@Composable
fun rememberInstanceFor(type: InstanceType): Instance? {
    val viewModel = viewModel<InstanceViewModel>()
    val instances by viewModel.allInstances.collectAsState()
    return instances.firstOrNull { i ->
        i.type == type && i.selected
    }
}

@Composable
fun rememberHasMultipleInstances(type: InstanceType): Boolean {
    val viewModel = viewModel<InstanceViewModel>()
    val instances by viewModel.allInstances.collectAsState()
    return instances.count { it.type == type } > 1
}