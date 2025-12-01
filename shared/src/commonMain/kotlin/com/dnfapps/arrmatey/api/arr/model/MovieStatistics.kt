package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieStatistics(
    val movieFileCount: Int,

    override val sizeOnDisk: Long,
    override val releaseGroups: List<String>
): ArrStatistics()
