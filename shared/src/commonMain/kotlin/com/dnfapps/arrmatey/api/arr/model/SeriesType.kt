package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.SerialName

enum class SeriesType {
    @SerialName("standard")
    Standard,

    @SerialName("daily")
    Daily,

    @SerialName("anime")
    Anime;

    companion object {
        fun allEntries() = entries.toList()
    }
}