package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class Revision(
    val version: Int,
    val real: Int,
    val isRepack: Boolean
)
