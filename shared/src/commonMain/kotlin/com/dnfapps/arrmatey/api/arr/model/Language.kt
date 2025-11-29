package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val id: Int,
    val name: String? = null
)
