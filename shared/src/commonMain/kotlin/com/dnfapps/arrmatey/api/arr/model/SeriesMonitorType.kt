package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.SerialName

enum class SeriesMonitorType {
    @SerialName("unknown")
    Unknown,

    @SerialName("all")
    All,

    @SerialName("future")
    Future,

    @SerialName("missing")
    Missing,

    @SerialName("existing")
    Existing,

    @SerialName("firstSeason")
    FirstSeason,

    @SerialName("lastSeason")
    LastSeason,

    @SerialName("latestSeason")
    LatestSeason,

    @SerialName("pilot")
    Pilot,

    @SerialName("recent")
    Recent,

    @SerialName("monitorSpecials")
    MonitorSpecials,

    @SerialName("unmonitorSpecials")
    UnmonitorSpecials,

    @SerialName("none")
    None,

    @SerialName("skip")
    Skip;

    companion object {
        fun allValues() = entries.toList()
    }
}