package com.dnfapps.arrmatey.api.arr.viewmodel

import com.dnfapps.arrmatey.api.arr.RadarrClient
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.arr.model.ExtraFile
import com.dnfapps.arrmatey.api.client.NetworkResult
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class RadarrRepository(instance: Instance): BaseArrRepository<ArrMovie>(instance) {

    override val client: RadarrClient by inject { parametersOf(instance) }

    private val _movieExtaFileMap = MutableStateFlow<Map<Int, List<ExtraFile>>>(emptyMap())
    val movieExtraFileMap: StateFlow<Map<Int, List<ExtraFile>>> = _movieExtaFileMap

    init {
        if (instance.type != InstanceType.Radarr) {
            throw IllegalArgumentException("Cannot instantiate RadarrViewModel with an instance of type ${instance.type}")
        }
    }

    suspend fun getMovieExtraFile(id: Int) {
        val response = client.getMovieExtraFile(id)
        if (response is NetworkResult.Success) {
            val newMap = _movieExtaFileMap.value.toMutableMap()
            newMap[id] = response.data
            _movieExtaFileMap.emit(newMap)
        }
    }

}