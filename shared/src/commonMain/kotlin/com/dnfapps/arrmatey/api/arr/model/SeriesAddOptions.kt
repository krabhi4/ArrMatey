package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class SeriesAddOptions(
    val monitor: SeriesMonitorType,
    val ignoreEpisodesWithFiles: Boolean = false,
    val ignoreEpisodesWithoutFiles: Boolean = false,
    val searchForMissingEpisodes: Boolean = false,
    val searchForCutoffUnmetEpisodes: Boolean = false
)
