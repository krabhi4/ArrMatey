package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ArrSeries(
    val id: Int,
    val alternateTitles: List<AlternateTitle>,
    val status: String,
    val ended: Boolean,
    val originalLanguage: Language,
    val year: Int,
    val qualityProfileId: Int,
    val seasonFolder: Boolean,
    val monitored: Boolean,
    val monitorNewItems: String,
    val useSceneNumbering: Boolean,
    val runtime: Int,
    val tvdbId: Int,
    val tvRageId: Int,
    val tvMazeId: Int,
    val tmdbId: Int,
    @Contextual val added: Instant,
    val ratings: Ratings,
    val seriesType: String,
    val images: List<ArrImage> = emptyList(),
    val seasons: List<Season> = emptyList(),
    val addOptions: AddOptions? = null,
    val title: String? = null,
    val sortTitle: String? = null,
    val profileName: String? = null,
    val overview: String? = null,
    val nextAiring: String? = null,
    val previousAiring: String? = null,
    val network: String? = null,
    val airTime: String? = null,
    val remotePoster: String? = null,
    val path: String? = null,
    val statistics: SeriesStatistics,
    val firstAired: String? = null,
    val lastAired: String? = null,
    val cleanTitle: String? = null,
    val imdbId: String? = null,
    val titleSlug: String? = null,
    val rootFolderPath: String? = null,
    val folder: String? = null,
    val certification: String? = null,
    val genres: List<String> = emptyList(),
    val tags: List<Int> = emptyList(),
    val episodesChanged: String? = null
)