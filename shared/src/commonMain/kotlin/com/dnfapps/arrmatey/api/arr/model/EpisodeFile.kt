package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class EpisodeFile(
    val id: Long,
    val seriesId: Long,
    val seasonNumber: Int,
    val relativePath: String? = null,
    val path: String? = null,
    val size: Long,
    @Contextual val dateAdded: Instant? = null,
    val sceneName: String? = null,
    val releaseGroup: String? = null,
    val languages: List<Language> = emptyList(),
    val quality: QualityInfo? = null,
    val customFormats: List<CustomFormat> = emptyList(),
    val customFormatScore: Int? = null,
    val indexerFlags: Int? = null,
    val releaseType: String? = null,
    val mediaInfo: MediaInfo? = null,
    val qualityCutoffNotMet: Boolean
) {
    val qualityName: String?
        get() = quality?.quality?.name
}
