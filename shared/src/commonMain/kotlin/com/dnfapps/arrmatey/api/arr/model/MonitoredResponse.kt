package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class MonitoredResponse(
    val id: Int,
    val monitored: Boolean
)
