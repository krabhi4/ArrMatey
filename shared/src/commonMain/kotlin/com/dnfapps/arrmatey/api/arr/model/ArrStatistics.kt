package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
sealed class ArrStatistics {
    abstract val sizeOnDisk: Long
    abstract val releaseGroups: List<String>
}