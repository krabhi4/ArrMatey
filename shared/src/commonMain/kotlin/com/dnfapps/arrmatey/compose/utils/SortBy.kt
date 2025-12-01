package com.dnfapps.arrmatey.compose.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import arrmatey.shared.generated.resources.Res
import arrmatey.shared.generated.resources.added
import arrmatey.shared.generated.resources.digital_release
import arrmatey.shared.generated.resources.file_size
import arrmatey.shared.generated.resources.grabbed
import arrmatey.shared.generated.resources.next_airing
import arrmatey.shared.generated.resources.previous_airing
import arrmatey.shared.generated.resources.rating
import arrmatey.shared.generated.resources.sort_ascending
import arrmatey.shared.generated.resources.sort_descending
import arrmatey.shared.generated.resources.title
import arrmatey.shared.generated.resources.year
import coil3.Image
import com.dnfapps.arrmatey.api.arr.model.ArrMedia
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.compose.icons.Hard_drive
import com.dnfapps.arrmatey.model.InstanceType
import org.jetbrains.compose.resources.StringResource

enum class SortBy(
    val iosIcon: String,
    val androidIcon: ImageVector,
    val textKey: String,
    val string: StringResource
) {
    Title("textformat", Icons.Default.SortByAlpha, "title", Res.string.title),
    Year("calendar", Icons.Default.CalendarMonth, "year", Res.string.year),
    Added("clock.fill", Icons.Filled.Schedule, "added", Res.string.added),
    Rating("star.fill", Icons.Filled.Star, "rating", Res.string.rating),
    FileSize("opticaldiscdrive.fill", Hard_drive, "file_size", Res.string.file_size),

    // Movies
    Grabbed("arrow.down.circle.fill", Icons.Default.ArrowCircleDown, "grabbed", Res.string.grabbed),
    DigitalRelease("play.tv", Icons.Default.Tv, "digital_release", Res.string.digital_release),

    // TV
    NextAiring("clock", Icons.Default.Schedule, "next_airing", Res.string.next_airing),
    PreviousAiring("clock.arrow.trianglehead.counterclockwise.rotate.90", Icons.Default.History, "previous_airing", Res.string.previous_airing);

    companion object {
        fun entries(type: InstanceType) =
            when (type) {
                InstanceType.Sonarr -> listOf(Title, Year, Added, Rating, FileSize, NextAiring, PreviousAiring)
                InstanceType.Radarr -> listOf(Title, Year, Added, Rating, FileSize, Grabbed, DigitalRelease)
            }

    }
}

enum class SortOrder(
    val iosIcon: String,
    val iosText: String,
    val androidIcon: ImageVector,
    val androidText: StringResource
) {
    Asc("arrow.up", "ascending", Icons.Default.ArrowUpward, Res.string.sort_ascending),
    Desc("arrow.down", "descending", Icons.Default.ArrowDownward, Res.string.sort_descending)
}

private fun List<ArrMedia<*,*,*,*>>.applyBaseSorting(sortBy: SortBy, order: SortOrder) = when(sortBy) {
    SortBy.Title -> if (order == SortOrder.Asc) sortedBy { it.title } else sortedByDescending { it.title }
    SortBy.Year -> if (order == SortOrder.Asc) sortedBy { it.year } else sortedByDescending { it.year }
    SortBy.Added -> if (order == SortOrder.Asc) sortedBy { it.added } else sortedByDescending { it.added }
    SortBy.Rating -> if (order == SortOrder.Asc) sortedBy { it.ratingScore() } else sortedByDescending { it.ratingScore() }
    SortBy.FileSize -> if (order == SortOrder.Asc) sortedBy { it.statistics.sizeOnDisk } else sortedByDescending { it.statistics.sizeOnDisk }
    else -> this
}

fun List<ArrSeries>.applySeriesSorting(sortBy: SortBy, order: SortOrder = SortOrder.Asc) = when(sortBy) {
    SortBy.Title, SortBy.Year, SortBy.Added, SortBy.Rating, SortBy.FileSize -> applyBaseSorting(sortBy, order)
    SortBy.NextAiring -> if (order == SortOrder.Asc) sortedBy { it.nextAiring } else sortedByDescending { it.nextAiring }
    SortBy.PreviousAiring -> if (order == SortOrder.Asc) sortedBy { it.previousAiring } else sortedByDescending { it.previousAiring }
    else -> this
}

fun List<ArrMovie>.applyMovieSorting(sortBy: SortBy, order: SortOrder = SortOrder.Asc) = when(sortBy) {
    SortBy.Title, SortBy.Year, SortBy.Added, SortBy.Rating, SortBy.FileSize -> applyBaseSorting(sortBy, order)
    SortBy.Grabbed -> if (order == SortOrder.Asc) sortedBy { it.movieFile?.dateAdded } else sortedByDescending { it.movieFile?.dateAdded }
    SortBy.DigitalRelease -> if (order == SortOrder.Asc) sortedBy { it.digitalRelease } else sortedByDescending { it.digitalRelease }
    else -> this
}