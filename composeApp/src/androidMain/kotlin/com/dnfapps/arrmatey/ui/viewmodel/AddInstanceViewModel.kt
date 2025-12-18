package com.dnfapps.arrmatey.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.compose.screens.viewmodel.AddInstanceRepository
import com.dnfapps.arrmatey.database.dao.InsertResult
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddInstanceViewModel: ViewModel(), KoinComponent {

    private val repository: AddInstanceRepository by inject()

    val saveButtonEnabled = repository.saveButtonEnabled
    val infoCardMap = repository.infoCardMap
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
    val endpointError = repository.endpointError
    val testing = repository.testing
    val result = repository.result
    val apiEndpoint = repository.apiEndpoint
    val apiKey = repository.apiKey
    val isSlowInstance = repository.isSlowInstance
    val customTimeout = repository.customTimeout
    val instanceLabel = repository.instanceLabel
    val createResult = repository.createResult
    val editResult = repository.editResult

    fun setApiEndpoint(value: String) = repository.setApiEndpoint(value)

    fun setApiKey(value: String) = repository.setApiKey(value)

    fun setIsSlowInstance(value: Boolean) = repository.setIsSlowInstance(value)

    fun setCustomTimeout(value: Long?) = repository.setCustomTimeout(value)

    fun setInstanceLabel(value: String) = repository.setInstanceLabel(value)

    fun testConnection() {
        viewModelScope.launch {
            repository.testConnection()
        }
    }

    fun reset() = repository.reset()

    fun dismissInfoCard(instanceType: InstanceType) = repository.dismissInfoCard(instanceType)

    fun createInstance(instanceType: InstanceType) {
        viewModelScope.launch {
            repository.createInstance(instanceType)
        }
    }

    fun updateInstance(instance: Instance) {
        viewModelScope.launch {
            repository.updateInstance(instance)
        }
    }

    fun initialize(instance: Instance) {
        setApiEndpoint(instance.url)
        setApiKey(instance.apiKey)
        setIsSlowInstance(instance.slowInstance)
        setCustomTimeout(instance.customTimeout)
        setInstanceLabel(instance.label)
    }

}