package com.dnfapps.arrmatey.arr.api.model

import com.dnfapps.arrmatey.shared.MR
import dev.icerock.moko.resources.format
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class LidarrRelease(
    override val id: Int? = null,
    override val guid: String,
    override val quality: QualityInfo,
    override val qualityWeight: Float,
    override val age: Float,
    override val ageHours: Float,
    override val ageMinutes: Float,
    override val size: Long,
    override val indexerId: Int,
    override val indexer: String,
    override val releaseGroup: String? = null,
    override val subGroup: String? = null,
    override val releaseHash: String? = null,
    override val title: String,
    override val sceneSource: Boolean,
    override val languages: List<Language> = emptyList(),
    override val approved: Boolean,
    override val temporarilyRejected: Boolean,
    override val rejected: Boolean,
    override val rejections: List<String> = emptyList(),
    @Contextual override val publishDate: Instant,
    override val commentUrl: String,
    override val downloadUrl: String,
    override val infoUrl: String,
    override val downloadAllowed: Boolean,
    override val releaseWeight: Float,
    override val customFormats: List<CustomFormat> = emptyList(),
    override val customFormatScore: Float,
    override val magnetUrl: String? = null,
    override val infoHash: String,
    override val seeders: Int,
    override val leechers: Int,
    override val protocol: ReleaseProtocol,
    override val downloadClientId: Int? = null,
    override val downloadClient: String? = null,
    override val shouldOverride: Boolean? = null,

    val artistName: String? = null,
    val albumTitle: String? = null,
    val discography: Boolean = false,
    val artistId: Long,
    val albumId: Long
): ArrRelease