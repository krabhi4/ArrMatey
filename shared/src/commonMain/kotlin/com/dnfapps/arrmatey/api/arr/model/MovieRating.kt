package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieRating(
    val votes: Int,
    val value: Double,
    val type: String
)
