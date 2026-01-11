package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
sealed class CommandPayload(val name: String) {
    @Serializable
    data class Movie(val movieIds: List<Int>): CommandPayload("MoviesSearch")
    @Serializable
    data class Series(val seriesId: Int): CommandPayload("SeriesSearch")
    @Serializable
    data class Season(val seriesId: Int, val seasonNumber: Int): CommandPayload("SeasonSearch")
    @Serializable
    data class Episode(val episodeIds: List<Long>): CommandPayload("EpisodeSearch")
    @Serializable
    data object RefreshMonitoredDownloads: CommandPayload("RefreshMonitoredDownloads")
}