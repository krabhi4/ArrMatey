package com.dnfapps.arrmatey.compose.utils

import arrmatey.shared.generated.resources.Res
import arrmatey.shared.generated.resources.all
import arrmatey.shared.generated.resources.continuing_only
import arrmatey.shared.generated.resources.downloaded
import arrmatey.shared.generated.resources.ended_only
import arrmatey.shared.generated.resources.missing
import arrmatey.shared.generated.resources.monitored
import arrmatey.shared.generated.resources.unmonitored
import arrmatey.shared.generated.resources.wanted
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.ArrMedia
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.api.arr.model.SeriesStatus
import com.dnfapps.arrmatey.model.InstanceType
import org.jetbrains.compose.resources.StringResource

enum class FilterBy(
    val iosText: String,
    val androidText: StringResource
) {
    All("all", Res.string.all),
    Monitored("monitored", Res.string.monitored),
    Unmonitored("unmonitored", Res.string.unmonitored),
    Missing("missing", Res.string.missing),

    // Movies
    Wanted("wanted", Res.string.wanted),
    Downloaded("downloaded", Res.string.downloaded),

    // Series
    ContinuingOnly("continuing_only", Res.string.continuing_only),
    EndedOnly("ended_only", Res.string.ended_only);

    companion object {
        fun typeEntries(type: InstanceType) =
            when (type) {
                InstanceType.Sonarr -> listOf(All, Monitored, Unmonitored, Missing, ContinuingOnly, EndedOnly)
                InstanceType.Radarr -> listOf(All, Monitored, Unmonitored, Missing, Wanted, Downloaded)
            }
    }
}

private fun List<AnyArrMedia>.applyBaseFiltering(filterBy: FilterBy) = when (filterBy) {
    FilterBy.All -> this
    FilterBy.Monitored -> filter { it.monitored }
    FilterBy.Unmonitored -> filter {!it.monitored }
    else -> this
}

fun List<ArrSeries>.applySeriesFiltering(filterBy: FilterBy) = when(filterBy) {
    FilterBy.Missing -> filter { it.episodeCount > it.episodeFileCount }
    FilterBy.ContinuingOnly -> filter { it.status == SeriesStatus.Continuing }
    FilterBy.EndedOnly -> filter { it.status == SeriesStatus.Ended }
    else -> applyBaseFiltering(filterBy) as List<ArrSeries>
}

fun List<ArrMovie>.applyMovieFiltering(filterBy: FilterBy) = when(filterBy) {
    FilterBy.Missing -> filter { it.monitored && it.isAvailable && it.movieFile == null }
    FilterBy.Wanted -> filter { it.monitored && it.movieFile == null }
    FilterBy.Downloaded -> filter { it.movieFile != null }
    else -> applyBaseFiltering(filterBy) as List<ArrMovie>
}

fun List<AnyArrMedia>.applyFiltering(type: InstanceType, filterBy: FilterBy) = when(type) {
    InstanceType.Sonarr -> (this as List<ArrSeries>).applySeriesFiltering(filterBy)
    InstanceType.Radarr -> (this as List<ArrMovie>).applyMovieFiltering(filterBy)
}