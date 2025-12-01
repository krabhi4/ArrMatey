package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieCollection(
    val title: String,
    val tmdbId: Int
)
