package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class ArrImage(
    val coverType: CoverType,
    val url: String? = null,
    val remoteUrl: String? = null
)
