package com.dnfapps.arrmatey.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.api.arr.model.ArrMedia
import com.dnfapps.arrmatey.api.arr.viewmodel.BaseArrRepository
import com.dnfapps.arrmatey.model.Instance
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class ArrViewModel(private val instance: Instance): ViewModel(), KoinComponent {
    private val repository: BaseArrRepository<out ArrMedia<*, *, *, *, *>> by inject {
        parametersOf(
            instance
        )
    }

    val uiState = repository.uiState

    fun refreshLibrary() {
        viewModelScope.launch {
            repository.refreshLibrary()
        }
    }
}

class ArrViewModelFactory(
    private val instance: Instance
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArrViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArrViewModel(instance) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}