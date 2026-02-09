package com.dnfapps.arrmatey.arr.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import com.dnfapps.arrmatey.shared.MR
import dev.icerock.moko.resources.StringResource

data class CalendarFilterState(
    val contentFilter: ContentFilter = ContentFilter.All,
    val showMonitoredOnly: Boolean = false,
    val showPremiersOnly: Boolean = false,
    val showFinalesOnly: Boolean = false,
    val instanceId: Long? = null
)

enum class CalendarViewMode {
    List, Month
}

enum class ContentFilter(
    val resource: StringResource,
    val imageVector: ImageVector
) {
    All(MR.strings.all, Icons.Default.VideoLibrary),
    MoviesOnly(MR.strings.movies, Icons.Default.Movie),
    EpisodesOnly(MR.strings.episodes, Icons.Default.Tv)
}