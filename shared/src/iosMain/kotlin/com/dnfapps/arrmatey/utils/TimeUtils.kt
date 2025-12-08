package com.dnfapps.arrmatey.utils

import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

actual fun getCurrentSystemTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}

actual fun is24Hour(): Boolean {
    val formatter = NSDateFormatter()
    formatter.dateStyle = NSDateFormatterNoStyle
    formatter.timeStyle = NSDateFormatterMediumStyle
    val testDate = NSDate()
    val formatted = formatter.stringFromDate(testDate)

    // Check if output contains AM/PM or similar 12h indicator
    return !formatted.contains("AM", true) && !formatted.contains("PM", true)
}

actual fun ArrSeries.formatNextAiringTime(): String? {
    if (nextAiring == null) return null
    val format = if (is24Hour()) "HH:mm MMMM d, yyyy" else "hh:mm a MMMM d, yyyy"
    val date = NSDate.dateWithTimeIntervalSince1970(nextAiring.toEpochMilliseconds().toDouble() / 1000.0)
    val formatter = NSDateFormatter()
    formatter.dateStyle = NSDateFormatterMediumStyle
    formatter.timeStyle = NSDateFormatterMediumStyle
    formatter.dateFormat = format
    return formatter.stringFromDate(date)
}

actual fun ArrMovie.formatReleaseDate(): String? {
    if (releaseDate == null) return null
    val format = "MMMM d, yyyy"
    val date = NSDate.dateWithTimeIntervalSince1970(releaseDate.toEpochMilliseconds().toDouble() / 1000.0)
    val formatter = NSDateFormatter()
    formatter.dateStyle = NSDateFormatterMediumStyle
    formatter.timeStyle = NSDateFormatterMediumStyle
    formatter.dateFormat = format
    return formatter.stringFromDate(date)
}