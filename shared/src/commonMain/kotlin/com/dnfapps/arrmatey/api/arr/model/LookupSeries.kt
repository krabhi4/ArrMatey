package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Instant

//@Serializable
//data class LookupSeries(
//    override val title: String,
//    override val sortTitle: String,
//    val status: SeriesStatus,
//    val ended: Boolean,
//    override val overview: String? = null,
//    val network: String? = null,
//    @Transient val airTime: Instant? = null,
//    override val images: List<ArrImage> = emptyList(),
//    override val year: Int,
//    override val runtime: Int,
//    val ratings: SeriesRatings
//): BaseMedia {
//    override fun ratingScore(): Double {
//        return ratings.value
//    }
//}
//
//@Serializable
//data class LookupMovie(
//    override val title: String,
//    override val sortTitle: String,
//    val status: MovieStatus,
//    override val overview: String? = null,
//    @Contextual val inCinemas: Instant? = null,
//    @Contextual val physicalRelease: Instant? = null,
//    @Contextual val digitalRelease: Instant? = null,
//    @Contextual val releaseDate: Instant? = null,
//    override val images: List<ArrImage>,
//    override val year: Int,
//    override val runtime: Int,
//    val ratings: MovieRatings
//): BaseMedia {
//    override fun ratingScore(): Double {
//        val imdb = ratings.imdb?.value
//        val rt = ratings.rottenTomatoes?.value?.apply { this/10 }
//        val tmdb = ratings.tmdb?.value
//        val mtc = ratings.metacritic?.value?.apply { this/10 }
//        val trakt = ratings.trakt?.value
//
//        val avail = listOfNotNull(imdb, rt, tmdb, mtc, trakt)
//        return avail.sum() / avail.size
//    }
//}
