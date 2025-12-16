package com.dnfapps.arrmatey.compose.screens.viewmodel

import com.dnfapps.arrmatey.PreferencesStore
import com.dnfapps.arrmatey.api.arr.BaseArrClient
import com.dnfapps.arrmatey.api.arr.GenericClient
import com.dnfapps.arrmatey.database.InstanceRepository
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.utils.isValidUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddInstanceRepository: KoinComponent {

    private val client: GenericClient by inject()
    private val instanceRepository: InstanceRepository by inject()
    private val preferencesStore: PreferencesStore by inject()

    private val _saveButtonEnabled = MutableStateFlow(false)
    val saveButtonEnabled: StateFlow<Boolean> = _saveButtonEnabled

    val infoCardMap = preferencesStore.showInfoCards

    private val _endpointError = MutableStateFlow(false)
    val endpointError: StateFlow<Boolean> = _endpointError

    private val _testing = MutableStateFlow(false)
    val testing: StateFlow<Boolean> = _testing

    private val _result = MutableStateFlow<Boolean?>(null)
    val result: StateFlow<Boolean?> = _result

    private val _apiEndpoint = MutableStateFlow("")
    val apiEndpoint: StateFlow<String> = _apiEndpoint

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey

    private val _isSlowInstance = MutableStateFlow(false)
    val isSlowInstance: StateFlow<Boolean> = _isSlowInstance

    private val _customTimeout = MutableStateFlow<Long?>(null)
    val customTimeout: StateFlow<Long?> = _customTimeout

    private val _instanceLabel = MutableStateFlow("")
    val instanceLabel = _instanceLabel

    fun setApiEndpoint(value: String) {
        _testing.value = false
        _result.value = null
        _apiEndpoint.value = value
        _saveButtonEnabled.value = false
    }

    fun setApiKey(value: String) {
        _testing.value = false
        _result.value = null
        _apiKey.value = value
        _saveButtonEnabled.value = false
    }

    fun setIsSlowInstance(value: Boolean) {
        _isSlowInstance.value = value
    }

    fun setCustomTimeout(value: Long?) {
        _customTimeout.value = value
    }

    fun setInstanceLabel(value: String) {
        _instanceLabel.value = value
    }

    suspend fun testConnection() {
        println("Testing connection")
        if (!testing.value) {
            println("can test")
            _endpointError.emit(false)
            if (apiEndpoint.value.isValidUrl()) {
                println("has values ${apiEndpoint.value} \n ${apiKey.value}")
                _apiEndpoint.value = apiEndpoint.value.trim()
                _apiKey.value = apiKey.value.trim()
                val r = client.test(apiEndpoint.value, apiKey.value)
                _testing.emit(false)
                _result.emit(r)
            } else {
                println("invalid api key")
                _testing.emit(false)
                _endpointError.emit(true)
            }
        }
        _saveButtonEnabled.emit(apiEndpoint.value.isNotEmpty() && apiKey.value.isNotEmpty() && result.value == true)
    }

    fun reset() {
        _result.value = null
        _saveButtonEnabled.value = false
        _endpointError.value = false
        _testing.value = false
        _apiEndpoint.value = ""
        _apiKey.value = ""
        instanceLabel.value = ""
        _isSlowInstance.value = false
        _customTimeout.value = null
    }

    fun dismissInfoCard(instanceType: InstanceType) {
        preferencesStore.dismissInfoCard(instanceType)
    }

    suspend fun saveInstance(instanceType: InstanceType) {
        val newInstance = Instance(
            type = instanceType,
            label = instanceLabel.value.takeUnless { it.isEmpty() },
            url = apiEndpoint.value,
            apiKey = apiKey.value,
            slowInstance = isSlowInstance.value,
            customTimeout = if (isSlowInstance.value) customTimeout.value else null
        )
        instanceRepository.newInstance(newInstance)
    }
}