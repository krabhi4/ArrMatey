package com.dnfapps.arrmatey.api.arr.viewmodel

import com.dnfapps.arrmatey.api.arr.SonarrClient
import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.api.arr.model.DownloadReleasePayload
import com.dnfapps.arrmatey.api.arr.model.Episode
import com.dnfapps.arrmatey.api.arr.model.ReleaseParams
import com.dnfapps.arrmatey.api.arr.model.SeriesRelease
import com.dnfapps.arrmatey.api.client.NetworkResult
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class SonarrRepository(instance: Instance): BaseArrRepository<ArrSeries, SeriesRelease, ReleaseParams.Series>(instance) {

    override val client: SonarrClient by inject { parametersOf(instance) }

    private val _episodeState = MutableStateFlow<EpisodeUiState>(EpisodeUiState.Initial)
    val episodeState: StateFlow<EpisodeUiState> = _episodeState

    init {
        if (instance.type != InstanceType.Sonarr) {
            throw IllegalArgumentException("Cannot instantiate SonarrViewModel with an instance of type ${instance.type}")
        }
    }

    suspend fun getEpisodes(seriesId: Int, seasonNumber: Int? = null) {
        _episodeState.emit(EpisodeUiState.Loading)
        val resp = client.getEpisodes(seriesId, seasonNumber)
        when (resp) {
            is NetworkResult.Success -> {
                _episodeState.value = EpisodeUiState.Success(items = resp.data)
            }
            is NetworkResult.HttpError -> {
                _episodeState.value = EpisodeUiState.Error(
                    error = ErrorEvent(resp.message ?: "Server error occurred"),
                    type = UiErrorType.Http
                )
            }
            is NetworkResult.NetworkError -> {
                _episodeState.value = EpisodeUiState.Error(
                    error = ErrorEvent(resp.message ?: "Network error occurred"),
                    type = UiErrorType.Network
                )
            }
            is NetworkResult.UnexpectedError -> {
                _episodeState.value = EpisodeUiState.Error(
                    error = ErrorEvent(resp.cause.message ?: "Unexpected error occurred"),
                    type = UiErrorType.Unexpected
                )
            }
        }
    }

    suspend fun toggleSeasonMonitorState(series: ArrSeries, seasonNumber: Int) {
        series.seasons.firstOrNull { it.seasonNumber == seasonNumber }?.let { season ->
            val newSeason = season.copy(monitored = !season.monitored)
            val newSeasons = series.seasons.toMutableList()
            val index = series.seasons.indexOfFirst { it.seasonNumber == seasonNumber }
            newSeasons.removeAt(index)
            newSeasons.add(newSeason)

            val newSeries = series.copy(seasons = newSeasons)
            val result = client.update(newSeries)
            if (result is NetworkResult.Success) {
                _detailUiState.value = DetailsUiState.Success(item = newSeries)
            }
        }
    }

    suspend fun toggleEpisodeMonitorState(episodeId: Long) {
        val state = episodeState.value
        if (state is EpisodeUiState.Success) {
            state.items.firstOrNull { it.id == episodeId }?.let { existingEpisode ->
                val newEpisode = existingEpisode.copy(monitored = !existingEpisode.monitored)
                val result = client.updateEpisode(newEpisode)
                if (result is NetworkResult.Success) {
                    val episodeList = state.items.map { episode ->
                        if (episode.id == episodeId) newEpisode else episode
                    }
                    _episodeState.value = EpisodeUiState.Success(items = episodeList)
                }
            }
        }
    }

    override suspend fun downloadRelease(release: SeriesRelease, force: Boolean) {
        _downloadReleaseState.value = DownloadState.Loading(release.guid)
        val payload = DownloadReleasePayload.Series(
            guid = release.guid,
            indexerId = release.indexerId,
            seriesId = release.seriesId,
            seasonNumber = release.seasonNumber,
            episodeId = release.episodeId
        )

        val response = client.downloadRelease(payload)
        when (response) {
            is NetworkResult.Success -> _downloadReleaseState.value = DownloadState.Success
            else -> _downloadReleaseState.value = DownloadState.Error
        }
        _downloadReleaseState.value = DownloadState.Initial
    }

}

sealed interface EpisodeUiState {
    data object Initial: EpisodeUiState
    data object Loading: EpisodeUiState
    data class Success(
        val items: List<Episode>
    ): EpisodeUiState
    data class Error(
        val error: ErrorEvent,
        val type: UiErrorType
    ): EpisodeUiState
}
