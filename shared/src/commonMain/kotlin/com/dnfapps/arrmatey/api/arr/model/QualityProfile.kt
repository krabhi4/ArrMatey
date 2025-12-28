package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class QualityProfile(
    val id: Int,
    val name: String? = null,
    val upgradeAllowed: Boolean = false,
    val cutoff: Int,
    val minFormatScore: Int,
    val cutoffFormatScore: Int,
    val minUpgradeFormatScore: Int,
    val formatItems: List<FormatItem>
)