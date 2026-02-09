package com.dnfapps.arrmatey.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.dnfapps.arrmatey.shared.MR
import dev.icerock.moko.resources.StringResource

enum class TabItem(
    val iosIcon: String,
    val androidIcon: ImageVector,
    val resource: StringResource
) {
    SHOWS("tv", Icons.Default.Tv, MR.strings.series),
    MOVIES("movieclapper", Icons.Default.Movie, MR.strings.movies),
    ACTIVITY("square.and.arrow.down", Icons.Default.Download, MR.strings.activity),
    CALENDAR("calendar", Icons.Default.CalendarMonth, MR.strings.schedule),
    SETTINGS("gear", Icons.Default.Settings, MR.strings.settings);

    companion object {
        fun allValues() = entries.toList()
    }
}