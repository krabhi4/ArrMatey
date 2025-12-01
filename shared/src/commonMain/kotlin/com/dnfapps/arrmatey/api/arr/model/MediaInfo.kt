package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaInfo(
    val audioBitrate: Int,
    val audioChannels: Double,
    val audioCodec: String,
    val audioLanguages: String,
    val audioStreamCount: Int,
    val videoBitDepth: Int,
    val videoBitrate: Int,
    val videoCodec: String,
    val videoFps: Double,
    val videoDynamicRange: String,
    val videoDynamicRangeType: String,
    val resolution: String,
    val runTime: String,
    val scanType: String,
    val subtitles: String
)
