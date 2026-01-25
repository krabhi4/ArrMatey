package com.dnfapps.arrmatey.compose.utils

enum class ReleaseFilterBy {
    Any,
    SeasonPack,
    SingleEpisode;

    companion object {
        fun allEntries() = entries.toList()
    }
}