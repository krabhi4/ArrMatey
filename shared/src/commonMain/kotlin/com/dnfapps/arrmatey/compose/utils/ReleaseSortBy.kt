package com.dnfapps.arrmatey.compose.utils

enum class ReleaseSortBy {
    Weight,
    Age,
    Quality,
    Seeders,
    FileSize,
    CustomScore;

    companion object {
        fun allEntries() = entries.toList()
    }
}