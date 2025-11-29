package com.dnfapps.arrmatey.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.dnfapps.arrmatey.api.arr.SonarrClient
import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class SonarrViewModel(instance: Instance): KoinComponent {

    init {
        if (instance.type != InstanceType.Sonarr) {
            throw IllegalArgumentException("Cannot instantiate SonarrViewModel with an instance of type ${instance.type}")
        }
    }

    private val sonarrClient: SonarrClient by inject { parametersOf(instance) }

    private val _library = MutableStateFlow<List<ArrSeries>>(emptyList())
    val library: StateFlow<List<ArrSeries>> = _library

    suspend fun refreshLibrary() {
        val newLibrary = sonarrClient.getLibrary()
        _library.emit(newLibrary)
    }

}

@Composable
fun rememberSonarrViewModel(instance: Instance): SonarrViewModel {
    return remember { SonarrViewModel(instance) }
}