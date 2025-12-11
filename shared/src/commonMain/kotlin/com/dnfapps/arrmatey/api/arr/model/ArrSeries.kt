package com.dnfapps.arrmatey.api.arr.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.ui.theme.SonarrContinuingAllDownloaded
import com.dnfapps.arrmatey.ui.theme.SonarrEndedAllDownloaded
import com.dnfapps.arrmatey.ui.theme.SonarrMissingEpsSeriesMonitored
import com.dnfapps.arrmatey.ui.theme.SonarrMissingEpsSeriesUnmonitored
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import kotlin.time.Instant

@Serializable
@Entity(
    tableName = "arr_series",
    foreignKeys = [
        ForeignKey(
            entity = Instance::class,
            parentColumns = ["id"],
            childColumns = ["instanceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("instanceId")]
)
data class ArrSeries(
    @PrimaryKey(autoGenerate = false)
    override val id: Int,
    override val title: String,
    override val originalLanguage: Language,
    override val year: Int,
    override val qualityProfileId: Int,
    override var monitored: Boolean,
    override val runtime: Int,
    override val tmdbId: Int,
    override val status: SeriesStatus,
    override val sortTitle: String? = null,
    override val overview: String? = null,
    override val path: String? = null,
    override val cleanTitle: String? = null,
    override val imdbId: String? = null,
    override val titleSlug: String? = null,
    override val rootFolderPath: String? = null,
    override val folder: String? = null,
    override val certification: String? = null,
    override val images: List<ArrImage> = emptyList(),
    override val alternateTitles: List<SeriesAlternateTitle> = emptyList(),
    override val genres: List<String> = emptyList(),
    override val tags: List<Int> = emptyList(),
    override val addOptions: SeriesAddOptions? = null,
    override val ratings: SeriesRatings,
    override val statistics: SeriesStatistics,
    @Contextual override val added: Instant,
    override var instanceId: Long? = null,

    val ended: Boolean,
    val seasonFolder: Boolean,
    val monitorNewItems: SeriesMonitorNewItems,
    val useSceneNumbering: Boolean,
    val tvdbId: Int,
    val tvRageId: Int,
    val tvMazeId: Int,
    val seriesType: SeriesType,
    val seasons: List<Season> = emptyList(),
    val profileName: String? = null,
    @Contextual val nextAiring: Instant? = null,
    @Contextual val previousAiring: Instant? = null,
    val network: String? = null,
    val airTime: String? = null,
    val remotePoster: String? = null,
    val firstAired: String? = null,
    val lastAired: String? = null,
    val episodesChanged: String? = null
): ArrMedia<SeriesAlternateTitle, SeriesAddOptions, SeriesRatings, SeriesStatistics, SeriesStatus>() {

    override fun ratingScore(): Double {
        return ratings.value
    }

    val episodeFileCount: Int
        get() = statistics.episodeFileCount

    val episodeCount: Int
        get() = statistics.episodeCount

    val seasonCount: Int
        get() = seasons.size

    override val statusString: String
        get() = status.name

    override val statusProgress: Float
        get() = statistics.percentOfEpisodes.toFloat()

    override val statusColor: Color
        get() = when {
            status == SeriesStatus.Ended && statistics.percentOfEpisodes == 100.toDouble() -> SonarrEndedAllDownloaded
            status == SeriesStatus.Continuing && statistics.percentOfEpisodes == 100.toDouble() -> SonarrContinuingAllDownloaded
            statistics.percentOfEpisodes != 100.toDouble() && monitored -> SonarrMissingEpsSeriesMonitored
            statistics.percentOfEpisodes != 100.toDouble() && !monitored -> SonarrMissingEpsSeriesUnmonitored
            else -> Color.Unspecified
        }

    override val releasedBy: String?
        get() = network

    override fun setMonitored(monitored: Boolean): ArrSeries {
        return copy(monitored = monitored)
    }

    // todo - include quality profiles/tags from instance
    override val infoItems: List<Info>
        get() = listOf(
            Info(
                label = "Series Type",
                value = seriesType.name
            ),
            Info(
                label = "Root Folder",
                value = rootFolderPath ?: "Unknown"
            ),
            Info(
                label = "New Seasons",
                value = if (monitorNewItems == SeriesMonitorNewItems.All) "Monitored" else "Unmonitored"
            ),
            Info(
                label = "Season Folders",
                value = if (seasonFolder) "Yes" else "No"
            )
        )

}