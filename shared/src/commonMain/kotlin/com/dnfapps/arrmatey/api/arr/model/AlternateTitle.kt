package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class AlternateTitle(
    val title: String? = null,
    val seasonNumber: Int? = null,
    val sceneSeasonNumber: Int? = null,
    val sceneOrigin: String? = null,
    val comment: String? = null
)
