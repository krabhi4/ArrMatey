package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ArrMovie(
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
    override val alternateTitles: List<MovieAlternateTitle> = emptyList(),
    override val genres: List<String> = emptyList(),
    override val tags: List<Int> = emptyList(),
    override val addOptions: MovieAddOptions? = null,
    override val ratings: MovieRatings,
    override val statistics: MovieStatistics,
    @Contextual override val added: Instant,

    val originalTitle: String,
    val secondaryYear: Int? = null,
    val secondaryYearSourceId: Int,
    val sizeOnDisk: Long,
    val inCinemas: String? = null,
    val physicalRelease: String? = null,
    @Contextual val digitalRelease: Instant? = null,
    val releaseDate: String? = null,
    val physicalReleaseNote: String? = null,
    val website: String,
    val remotePoster: String? = null,
    val youTubeTrailerId: String,
    val studio: String,
    val hasFile: Boolean,
    val movieFileId: Int,
    val minimumAvailability: String,
    val isAvailable: Boolean,
    val folderName: String,
    val keywords: List<String> = emptyList(),
    val movieFile: MovieFile? = null,
    val collection: MovieCollection? = null,
    val popularity: Double,
    val lastSearchTime: String? = null,
): ArrMedia<MovieAlternateTitle, MovieAddOptions, MovieRatings, MovieStatistics>() {

    override fun ratingScore(): Double {
        val imdb = ratings.imdb?.value
        val rt = ratings.rottenTomatoes?.value?.apply { this/10 }
        val tmdb = ratings.tmdb?.value
        val mtc = ratings.metacritic?.value?.apply { this/10 }
        val trakt = ratings.trakt?.value

        val avail = listOfNotNull(imdb, rt, tmdb, mtc, trakt)
        return avail.sum() / avail.size
    }

}