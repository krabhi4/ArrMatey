package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class MovieFile(
    val id: Int,
    val movieId: Int,
    val relativePath: String,
    val path: String,
    val size: Long,
    @Contextual val dateAdded: Instant,
    val sceneName: String? = null,
    val releaseGroup: String? = null,
    val edition: String,
    val languages: List<Language> = emptyList(),
    val quality: QualityInfo,
    val customFormats: List<CustomFormat> = emptyList(),
    val customFormatScore: Int? = null,
    val indexerFlags: Int,
    val mediaInfo: MediaInfo? = null,
    val originalFilePath: String? = null,
    val qualityCutoffNotMet: Boolean
)
