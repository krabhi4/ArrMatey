package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieRatings(
    val imdb: MovieRating? = null,
    val tmdb: MovieRating? = null,
    val metacritic: MovieRating? = null,
    val rottenTomatoes: MovieRating? = null,
    val trakt: MovieRating? = null
)

enum class RatingType {
    Imdb,
    Tmdb,
    Metacritic,
    RottenTomatoes,
    Trakt
}
