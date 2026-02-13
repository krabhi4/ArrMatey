package com.dnfapps.arrmatey.arr.api.model

import androidx.compose.ui.graphics.Color
import com.dnfapps.arrmatey.arr.api.client.HasArrImages
import com.dnfapps.arrmatey.ui.theme.SonarrContinuingAllDownloaded
import com.dnfapps.arrmatey.ui.theme.SonarrEndedAllDownloaded
import com.dnfapps.arrmatey.ui.theme.SonarrMissingEpsSeriesMonitored
import com.dnfapps.arrmatey.ui.theme.SonarrMissingEpsSeriesUnmonitored
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Arrtist(
    override val id: Long? = null,
    @SerialName("artistName") override val title: String,
    @SerialName("sortName") override val sortTitle: String,
    @SerialName("cleanName") override val cleanTitle: String? = null,
    override val originalLanguage: Language? = null,
    override val year: Int? = null,
    override val runtime: Int? = null,
    override val certification: String? = null,
    override val alternateTitles: List<AlternateTitle> = emptyList(),

    override val qualityProfileId: Int,
    override val monitored: Boolean,
    override val images: List<ArrImage>,
    override val overview: String? = null,
    override val path: String? = null,
    override val titleSlug: String? = null,
    override val rootFolderPath: String? = null,
    override val folder: String? = null,
    override val genres: List<String> = emptyList(),
    override val tags: List<Int> = emptyList(),
    override val ratings: LidarrRatings,
    override val statistics: LidarrStatistics? = null,
    @Contextual override val added: Instant? = null,
    override val status: MediaStatus,

    val monitorNewItems: ArtistMonitorType,
    val metadataProfileId: Int,
    val foreignArtistId: String? = null,
    val mbId: String? = null,
    val tadbId: Long = 0,
    val discogsId: Long = 0,
    val artistType: String? = null,
    val links: List<ArrLink> = emptyList(),
    val nextAlbum: ArrAlbum? = null,
    val lastAlbum: ArrAlbum? = null,
    val members: List<ArtistMember> = emptyList(),
    val addOptions: ArtistAddOptions? = null
): ArrMedia, HasArrImages<Arrtist> {
    override val guid: Long get() = id ?: (tadbId + 100_000)

    override val isMissing: Boolean
        get() = statistics?.let { it.trackFileCount < it.totalTrackCount } ?: false

    override fun ratingScore(): Double = ratings.value.toDouble()

    override val statusColor: Color
        get() = when {
            status == MediaStatus.Ended && statistics?.percentOfTracks == 100f -> SonarrEndedAllDownloaded
            status == MediaStatus.Continuing && statistics?.percentOfTracks == 100f -> SonarrContinuingAllDownloaded
            statistics?.percentOfTracks != 100f && monitored -> SonarrMissingEpsSeriesMonitored
            statistics?.percentOfTracks != 100f && !monitored -> SonarrMissingEpsSeriesUnmonitored
            else -> Color.Unspecified
        }

    override val releasedBy: String? get() = null
    override val statusString: String get() = status.name

    override fun setMonitored(monitored: Boolean): ArrMedia = this.copy(monitored = monitored)

    val trackFileCount: Int
        get() = statistics?.trackFileCount ?: 0

    val trackCount: Int
        get() = statistics?.trackCount ?: 0

    val albumCount: Int
        get() = statistics?.albumCount ?: 0

    override val statusProgress: Float
        get() = statistics?.percentOfTracks?.div(100f) ?: 0f

    fun copyForCreation(
        monitor: ArtistMonitorType,
        monitorNew: ArtistMonitorType,
        qualityProfileId: Int,
        rootFolderPath: String?
    ) = copy(
        id = 0,
        addOptions = ArtistAddOptions(monitor = monitor),
        monitorNewItems = monitorNew,
        qualityProfileId = qualityProfileId,
        rootFolderPath = rootFolderPath,
        metadataProfileId = 1
    )

    fun copyForEdit(
        monitored: Boolean,
        monitorNew: ArtistMonitorType,
        qualityProfileId: Int,
        rootFolderPath: String?
    ) = copy(
        monitored = monitored,
        monitorNewItems = monitorNew,
        qualityProfileId = qualityProfileId,
        rootFolderPath = rootFolderPath
    )

    override fun withLocalImages(instanceUrl: String): Arrtist  =
        copy(images = images.map { it.rebuildWithLocalUrls(instanceUrl) })

}