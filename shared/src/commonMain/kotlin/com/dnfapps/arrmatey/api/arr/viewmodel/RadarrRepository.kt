package com.dnfapps.arrmatey.api.arr.viewmodel

import com.dnfapps.arrmatey.api.arr.RadarrClient
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.arr.model.DownloadReleasePayload
import com.dnfapps.arrmatey.api.arr.model.ExtraFile
import com.dnfapps.arrmatey.api.arr.model.MovieRelease
import com.dnfapps.arrmatey.api.arr.model.ReleaseParams
import com.dnfapps.arrmatey.api.client.NetworkResult
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class RadarrRepository(instance: Instance): BaseArrRepository<ArrMovie, MovieRelease, ReleaseParams.Movie>(instance) {

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

    override suspend fun downloadRelease(release: MovieRelease, force: Boolean) {
        _downloadReleaseState.value = DownloadState.Loading(release.guid)
        val payload = DownloadReleasePayload.Movie(
            guid = release.guid,
            indexerId = release.indexerId,
            movieId = if (force) release.movieId else null
        )

        val response = client.downloadRelease(payload)
        when (response) {
            is NetworkResult.Success -> _downloadReleaseState.value = DownloadState.Success
            else -> _downloadReleaseState.value = DownloadState.Error
        }
        _downloadReleaseState.value = DownloadState.Initial
    }

}