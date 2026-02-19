package com.dnfapps.arrmatey.arr.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import com.dnfapps.arrmatey.shared.MR
import dev.icerock.moko.resources.StringResource

data class CalendarFilterState(
    val viewMode: CalendarViewMode = CalendarViewMode.List,
    val contentFilter: ContentFilter = ContentFilter.All,
    val showMonitoredOnly: Boolean = false,
    val showPremiersOnly: Boolean = false,
    val showFinalesOnly: Boolean = false,
    val instanceId: Long? = null
) {
    constructor(): this(CalendarViewMode.List, ContentFilter.All, false, false, false, null)
}

enum class CalendarViewMode {
    List, Month
}

enum class ContentFilter(
    val resource: StringResource,
    val imageVector: ImageVector,
    val systemImage: String
) {
    All(MR.strings.all, Icons.Default.VideoLibrary, "play.square.stack"),
    MoviesOnly(MR.strings.movies, Icons.Default.Movie, "movieclapper"),
    EpisodesOnly(MR.strings.episodes, Icons.Default.Tv, "tv"),
    AlbumsOnly(MR.strings.albums_header, Icons.Default.MusicNote, "music.note")
}