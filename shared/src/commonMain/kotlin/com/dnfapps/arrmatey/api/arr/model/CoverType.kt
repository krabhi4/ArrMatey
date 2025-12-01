package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.SerialName

enum class CoverType {
    @SerialName("clearlogo")
    ClearLogo,

    @SerialName("banner")
    Banner,

    @SerialName("poster")
    Poster,

    @SerialName("fanart")
    FanArt,

    Undefined
}