package com.dnfapps.arrmatey.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.dnfapps.arrmatey.api.arr.RadarrClient
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class RadarrViewModel(instance: Instance): KoinComponent, IArrViewModel {

    init {
        if (instance.type != InstanceType.Radarr) {
            throw IllegalArgumentException("Cannot instantiate RadarrViewModel with an instance of type ${instance.type}")
        }
    }

    private val radarrClient: RadarrClient by inject { parametersOf(instance) }

    private val _library = MutableStateFlow<List<ArrMovie>>(emptyList())
    val library: StateFlow<List<ArrMovie>> = _library

    override suspend fun refreshLibrary() {
        val newLibrary = radarrClient.getLibrary()
        _library.emit(newLibrary)
    }
}

@Composable
fun rememberRadarrViewModel(instance: Instance): RadarrViewModel {
    return remember { RadarrViewModel(instance) }
}