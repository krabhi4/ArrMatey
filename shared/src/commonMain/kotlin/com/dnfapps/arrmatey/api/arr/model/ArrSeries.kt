package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ArrSeries(
    override val id: Int,
    override val title: String,
    override val originalLanguage: Language,
    override val year: Int,
    override val qualityProfileId: Int,
    override val monitored: Boolean,
    override val runtime: Int,
    override val tmdbId: Int,
    override val status: String,
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

    val ended: Boolean,
    val seasonFolder: Boolean,
    val monitorNewItems: String,
    val useSceneNumbering: Boolean,
    val tvdbId: Int,
    val tvRageId: Int,
    val tvMazeId: Int,
    val seriesType: String,
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
): ArrMedia<SeriesAlternateTitle, SeriesAddOptions, SeriesRatings, SeriesStatistics>() {

    override fun ratingScore(): Double {
        return ratings.value
    }

}