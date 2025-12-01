package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieAddOptions(
    val ignoreEpisodesWithFiles: Boolean,
    val ignoreEpisodesWithoutFiles: Boolean,
    val monitor: String,
    val searchForMovie: Boolean,
    val addMethod: String
)
