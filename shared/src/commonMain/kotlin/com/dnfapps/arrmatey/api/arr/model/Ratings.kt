package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class Ratings(
    val votes: Int,
    val value: Double
)
