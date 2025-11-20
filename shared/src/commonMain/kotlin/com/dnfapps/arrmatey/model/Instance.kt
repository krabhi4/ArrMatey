package com.dnfapps.arrmatey.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Instance(
    val type: InstanceType,
    var label: String,
    var url: String,
    var apiKey: String
) {
    val id = Uuid.random()
}

enum class InstanceType {
    Sonarr
}