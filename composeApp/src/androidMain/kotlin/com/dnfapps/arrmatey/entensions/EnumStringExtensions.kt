package com.dnfapps.arrmatey.entensions

import androidx.annotation.StringRes
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.api.arr.model.MovieStatus
import com.dnfapps.arrmatey.api.arr.model.SeriesMonitorType
import com.dnfapps.arrmatey.api.arr.model.SeriesType
import org.jetbrains.compose.resources.StringResource

@StringRes
fun MovieStatus.stringResource() = when (this) {
    MovieStatus.Tba -> R.string.tba
    MovieStatus.Deleted -> R.string.deleted
    MovieStatus.Released -> R.string.released
    MovieStatus.Announced -> R.string.announced
    MovieStatus.InCinemas -> R.string.in_cinemas
}

fun SeriesMonitorType.stringResource() = when (this) {
    SeriesMonitorType.Unknown -> R.string.unknown
    SeriesMonitorType.All -> R.string.all
    SeriesMonitorType.Future -> R.string.future
    SeriesMonitorType.Missing -> R.string.missing
    SeriesMonitorType.Existing -> R.string.existing
    SeriesMonitorType.FirstSeason -> R.string.first_season
    SeriesMonitorType.LastSeason -> R.string.last_season
    SeriesMonitorType.LatestSeason -> R.string.latest_seasons
    SeriesMonitorType.Pilot -> R.string.pilot
    SeriesMonitorType.Recent -> R.string.recent
    SeriesMonitorType.UnmonitorSpecials -> R.string.unmonitor_specials
    SeriesMonitorType.MonitorSpecials -> R.string.monitor_specials
    SeriesMonitorType.None -> R.string.none
    SeriesMonitorType.Skip -> R.string.skip
}

fun SeriesType.stringResource() = when (this) {
    SeriesType.Standard -> R.string.standard
    SeriesType.Daily -> R.string.daily
    SeriesType.Anime -> R.string.anime
}