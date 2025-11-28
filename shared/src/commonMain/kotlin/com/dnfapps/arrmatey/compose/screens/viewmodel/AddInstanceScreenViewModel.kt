package com.dnfapps.arrmatey.compose.screens.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.dnfapps.arrmatey.api.sonarr.SonarrClient
import com.dnfapps.arrmatey.database.dao.InstanceDao
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.utils.isValidUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddInstanceScreenViewModel : ViewModel(), KoinComponent {

    val client: SonarrClient by inject()
    val instanceDao: InstanceDao by inject()

    private val _saveButtonEnabled = MutableStateFlow(false)
    val saveButtonEnabled: StateFlow<Boolean> = _saveButtonEnabled

    // todo - get and store this in preferences per instance
    private val _showInfoCard = MutableStateFlow(true)
    val showInfoCard: StateFlow<Boolean> = _showInfoCard

    private val _endpointError = MutableStateFlow(false)
    val endpointError: StateFlow<Boolean> = _endpointError

    private val _testing = MutableStateFlow(false)
    val testing: StateFlow<Boolean> = _testing

    private val _result = MutableStateFlow<Boolean?>(null)
    val result: StateFlow<Boolean?> = _result

    private val _apiEndpoint = MutableStateFlow("")
    val apiEndpoint: StateFlow<String> = _apiEndpoint

    fun setApiEndpoint(value: String) {
        _testing.value = false
        _result.value = null
        _apiEndpoint.value = value
    }

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey

    fun setApiKey(value: String) {
        _testing.value = false
        _result.value = null
        _apiKey.value = value
    }

    val instanceLabel = mutableStateOf("")

    fun testConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!testing.value) {
                _endpointError.emit(false)
                if (apiEndpoint.value.isValidUrl()) {
                    _apiEndpoint.value = apiEndpoint.value.trim()
                    _apiKey.value = apiKey.value.trim()
                    val r = client.test(apiEndpoint.value, apiKey.value)
                    _testing.emit(false)
                    _result.emit(r)
                } else {
                    _testing.emit(false)
                    _endpointError.emit(true)
                }
            }
        }
    }

    fun reset() {
        _result.value = null
        _saveButtonEnabled.value = false
        _endpointError.value = false
        _testing.value = false
        _apiEndpoint.value = ""
        _apiKey.value = ""
        instanceLabel.value = ""
    }

    fun dismissInfoCard(instanceType: InstanceType) {
        _showInfoCard.value = false
    }

    suspend fun saveInstance(instanceType: InstanceType) {
        val newInstance = Instance(
            type = instanceType,
            label = instanceLabel.value.takeUnless { it.isEmpty() },
            url = apiEndpoint.value,
            apiKey = apiKey.value
        )
        instanceDao.insert(newInstance)
    }
}