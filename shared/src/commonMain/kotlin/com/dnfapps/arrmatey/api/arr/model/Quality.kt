package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class Quality(
    val id: Int,
    val name: String,
    val source: String,
    val resolution: Int,
    val modifier: String
)
