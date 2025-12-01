package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
sealed class ArrMedia<AT, AO, R, ST: ArrStatistics> {
    abstract val id: Int
    abstract val title: String
    abstract val originalLanguage: Language
    abstract val year: Int
    abstract val qualityProfileId: Int
    abstract val monitored: Boolean
    abstract val runtime: Int
    abstract val tmdbId: Int
    abstract val status: String
    abstract val images: List<ArrImage>
    abstract val sortTitle: String?
    abstract val overview: String?
    abstract val path: String?
    abstract val cleanTitle: String?
    abstract val imdbId: String?
    abstract val titleSlug: String?
    abstract val rootFolderPath: String?
    abstract val folder: String?
    abstract val certification: String?
    abstract val genres: List<String>
    abstract val tags: List<Int>
    abstract val alternateTitles: List<AT>
    abstract val addOptions: AO?
    abstract val ratings: R
    abstract val statistics: ST
    @Contextual abstract val added: Instant

    abstract fun ratingScore(): Double
}