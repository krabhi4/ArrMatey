package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class QueueStatusMessage(
    val title: String? = null,
    val messages: List<String> = emptyList()
)
