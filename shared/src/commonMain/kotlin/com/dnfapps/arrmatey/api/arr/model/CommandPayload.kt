package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
sealed class CommandPayload(val name: String) {
    @Serializable
    data class RadarrSearch(val movieIds: List<Int>): CommandPayload("MoviesSearch")
    @Serializable
    data class SonarrSearch(val seriesIds: List<Int>): CommandPayload("SeriesSearch")
}