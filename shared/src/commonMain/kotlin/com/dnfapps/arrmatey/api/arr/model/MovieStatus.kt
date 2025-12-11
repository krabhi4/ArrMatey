package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.SerialName

enum class MovieStatus {
    @SerialName("tba")
    Tba,
    @SerialName("announced")
    Announced,
    @SerialName("inCinemas")
    InCinemas,
    @SerialName("released")
    Released,
    @SerialName("deleted")
    Deleted
}