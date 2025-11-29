package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class SeriesStatistics(
    val seasonCount: Int,
    val episodeFileCount: Int,
    val episodeCount: Int,
    val totalEpisodeCount: Int,
    val sizeOnDisk: Long,
    val percentOfEpisodes: Double,
    val releaseGroups: List<String> = emptyList()
)
