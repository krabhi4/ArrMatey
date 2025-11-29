package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class AddOptions(
    val monitor: String,
    val ignoreEpisodesWithFiles: Boolean = false,
    val ignoreEpisodesWithoutFiles: Boolean = false,
    val searchForMissingEpisodes: Boolean = false,
    val searchForCutoffUnmetEpisodes: Boolean = false
)
