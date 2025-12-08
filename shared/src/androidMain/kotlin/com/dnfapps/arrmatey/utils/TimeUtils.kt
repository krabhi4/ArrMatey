package com.dnfapps.arrmatey.utils

import android.icu.text.SimpleDateFormat
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import java.util.Date
import java.util.Locale

actual fun getCurrentSystemTimeMillis(): Long = System.currentTimeMillis()

actual fun is24Hour(): Boolean = DateHelper.is24Hour()

actual fun ArrSeries.formatNextAiringTime(): String? {
    if (nextAiring == null) return null
    val format = if (is24Hour()) "HH:mm MMMM d, yyyy" else "h:mm a MMMM d, yyyy"
    val date = Date(nextAiring.toEpochMilliseconds())
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(date)
}

actual fun ArrMovie.formatReleaseDate(): String? {
    if (releaseDate == null) return null
    val format = "MMMM d, yyyy"
    val date = Date(releaseDate.toEpochMilliseconds())
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(date)
}