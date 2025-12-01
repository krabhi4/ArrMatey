package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieAlternateTitle(
    val id: Int,
    val sourceType: String,
    val movieMetadataId: Int,
    val title: String,
    val cleanTitle: String? = null
)
