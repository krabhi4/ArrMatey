package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: Int,
    val label: String
)
