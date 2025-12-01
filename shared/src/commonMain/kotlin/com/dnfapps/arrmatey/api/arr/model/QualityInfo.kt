package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class QualityInfo(
    val quality: Quality,
    val revision: Revision
)
