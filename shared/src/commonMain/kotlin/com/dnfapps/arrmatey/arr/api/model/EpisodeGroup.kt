package com.dnfapps.arrmatey.arr.api.model

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeGroup(
    val first: Episode,
    val additional: List<Episode>,
    val totalCount: Int = 1 + additional.size
)