package com.dnfapps.arrmatey.arr.usecase

import com.dnfapps.arrmatey.arr.api.model.ArrAlbum
import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.api.model.ArrMovie
import com.dnfapps.arrmatey.arr.api.model.ArrSeries
import com.dnfapps.arrmatey.arr.api.model.Arrtist
import com.dnfapps.arrmatey.arr.api.model.Episode
import com.dnfapps.arrmatey.client.NetworkResult
import com.dnfapps.arrmatey.instances.repository.InstanceScopedRepository

class ToggleMonitorUseCase {
    suspend fun toggleMedia(
        item: ArrMedia,
        repository: InstanceScopedRepository
    ): NetworkResult<ArrMedia> {
        val updatedItem = when (item) {
            is ArrSeries -> item.copy(monitored = !item.monitored)
            is ArrMovie -> item.copy(monitored = !item.monitored)
            is Arrtist -> item.copy(monitored = !item.monitored)
        }
        return repository.updateMediaItem(updatedItem)
    }

    suspend fun toggleSeason(
        seriesId: Long,
        seasonNumber: Int,
        repository: InstanceScopedRepository
    ): NetworkResult<ArrMedia> {
        return repository.toggleSeasonMonitor(seriesId, seasonNumber)
    }

    suspend fun toggleEpisode(
        episode: Episode,
        repository: InstanceScopedRepository
    ): NetworkResult<Episode> {
        return repository.toggleEpisodeMonitor(episode)
    }

    suspend fun toggleAlbum(
        album: ArrAlbum,
        repository: InstanceScopedRepository
    ): NetworkResult<ArrAlbum> {
        return repository.toggleAlbumMonitor(album)
    }
}