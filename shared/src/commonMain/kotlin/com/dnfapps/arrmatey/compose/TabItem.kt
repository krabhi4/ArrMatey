package com.dnfapps.arrmatey.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector

enum class TabItem(
    val iosIcon: String,
    val androidIcon: ImageVector,
    val textKey: String
) {
    SHOWS("tv", Icons.Default.Tv, "series"),
    MOVIES("movieclapper", Icons.Default.Movie, "movies"),
    ACTIVITY("square.and.arrow.down", Icons.Default.Download, "activity"),
    SETTINGS("gear", Icons.Default.Settings, "settings");

    companion object {
        fun allValues() = entries.toList()
    }
}