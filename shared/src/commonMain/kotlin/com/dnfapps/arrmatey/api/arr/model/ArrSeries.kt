package com.dnfapps.arrmatey.api.arr.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import arrmatey.shared.generated.resources.Res
import arrmatey.shared.generated.resources.genres
import arrmatey.shared.generated.resources.monitored
import arrmatey.shared.generated.resources.new_seasons
import arrmatey.shared.generated.resources.no
import arrmatey.shared.generated.resources.path
import arrmatey.shared.generated.resources.root_folder
import arrmatey.shared.generated.resources.season_folders
import arrmatey.shared.generated.resources.series_type
import arrmatey.shared.generated.resources.unknown
import arrmatey.shared.generated.resources.unmonitored
import arrmatey.shared.generated.resources.yes
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.ui.theme.SonarrContinuingAllDownloaded
import com.dnfapps.arrmatey.ui.theme.SonarrEndedAllDownloaded
import com.dnfapps.arrmatey.ui.theme.SonarrMissingEpsSeriesMonitored
import com.dnfapps.arrmatey.ui.theme.SonarrMissingEpsSeriesUnmonitored
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import kotlin.time.Instant

@Serializable
data class ArrSeries(
    override val id: Int? = null,
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
    override val statistics: SeriesStatistics? = null,
    @Contextual override val added: Instant,

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
        get() = statistics?.episodeFileCount ?: 0

    val episodeCount: Int
        get() = statistics?.episodeCount ?: 0

    val seasonCount: Int
        get() = seasons.size

    override val statusString: String
        get() = status.name

    override val statusProgress: Float
        get() = statistics?.percentOfEpisodes?.toFloat() ?: 0f

    override val statusColor: Color
        get() = when {
            status == SeriesStatus.Ended && statistics?.percentOfEpisodes == 100.toDouble() -> SonarrEndedAllDownloaded
            status == SeriesStatus.Continuing && statistics?.percentOfEpisodes == 100.toDouble() -> SonarrContinuingAllDownloaded
            statistics?.percentOfEpisodes != 100.toDouble() && monitored -> SonarrMissingEpsSeriesMonitored
            statistics?.percentOfEpisodes != 100.toDouble() && !monitored -> SonarrMissingEpsSeriesUnmonitored
            else -> Color.Unspecified
        }

    override val releasedBy: String?
        get() = network

    override fun setMonitored(monitored: Boolean): ArrSeries {
        return copy(monitored = monitored)
    }

    // todo - include quality profiles/tags from instance
    override val infoItems: Flow<List<Info>>
        get() = _infoItems

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newInfo = listOf(
                    Info(
                        label = getString(Res.string.series_type),
                        value = seriesType.name
                    ),
                    Info(
                        label = getString(Res.string.root_folder),
                        value = rootFolderPath ?: getString(Res.string.unknown)
                    ),
                    Info(
                        label = getString(Res.string.path),
                        value = path ?: getString(Res.string.unknown)
                    ),
                    Info(
                        label = getString(Res.string.new_seasons),
                        value = if (monitorNewItems == SeriesMonitorNewItems.All) {
                            getString(Res.string.monitored)
                        } else {
                            getString(Res.string.unmonitored)
                        }
                    ),
                    Info(
                        label = getString(Res.string.season_folders),
                        value = if (seasonFolder) getString(Res.string.yes) else getString(Res.string.no)
                    )
                )
                _infoItems.emit(newInfo)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

}