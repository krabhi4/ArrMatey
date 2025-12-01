package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class SelectOption(
    val value: Int,
    val name: String,
    val order: Int,
    val hint: String,
    val dividerAfter: Boolean
)
