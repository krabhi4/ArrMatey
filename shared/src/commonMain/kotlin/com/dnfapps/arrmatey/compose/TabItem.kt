package com.dnfapps.arrmatey.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.dnfapps.arrmatey.shared.MR
import dev.icerock.moko.resources.StringResource

enum class TabItem(
    val iosIcon: String,
    val androidIcon: ImageVector,
    val resource: StringResource,
    val drawerOnly: Boolean = false
) {
    SHOWS("tv", Icons.Default.Tv, MR.strings.series),
    MOVIES("movieclapper", Icons.Default.Movie, MR.strings.movies),
    MUSIC("music.quarternote.3", Icons.Default.MusicNote, MR.strings.music),
    ACTIVITY("square.and.arrow.down", Icons.Default.Download, MR.strings.activity),
    CALENDAR("calendar", Icons.Default.CalendarMonth, MR.strings.schedule),
    SETTINGS("gear", Icons.Default.Settings, MR.strings.settings, drawerOnly = true);

    companion object {
        val bottomEntries = entries.filter { !it.drawerOnly }
    }
}