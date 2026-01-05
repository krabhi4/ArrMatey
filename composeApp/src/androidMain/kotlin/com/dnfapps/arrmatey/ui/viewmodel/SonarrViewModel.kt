package com.dnfapps.arrmatey.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.api.arr.model.CommandPayload
import com.dnfapps.arrmatey.api.arr.viewmodel.SonarrRepository
import com.dnfapps.arrmatey.model.Instance
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class SonarrViewModel(instance: Instance): ArrViewModel(instance) {

    private val sonarrRepo = repository as SonarrRepository

    val episodeState = sonarrRepo.episodeState

    fun getEpisodes(seriesId: Int, seasonNumber: Int? = null) {
        viewModelScope.launch {
            sonarrRepo.getEpisodes(seriesId, seasonNumber)
        }
    }

    override fun getDetails(id: Int) {
        super.getDetails(id)
        getEpisodes(id)
    }

    override fun searchPayload(ids: List<Int>): CommandPayload {
        return CommandPayload.SonarrSearch(ids)
    }

    @OptIn(ExperimentalTime::class)
    fun toggleSeasonMonitor(series: ArrSeries, seasonNumber: Int) {
        viewModelScope.launch {
            sonarrRepo.toggleSeasonMonitorState(series, seasonNumber)
        }
    }

    fun toggleEpisodeMonitor(episodeId: Long) {
        viewModelScope.launch {
            sonarrRepo.toggleEpisodeMonitorState(episodeId)
        }
    }

}