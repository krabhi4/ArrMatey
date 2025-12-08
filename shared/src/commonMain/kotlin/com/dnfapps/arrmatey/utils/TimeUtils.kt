package com.dnfapps.arrmatey.utils

import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.arr.model.ArrSeries

expect fun getCurrentSystemTimeMillis(): Long

expect fun is24Hour(): Boolean

expect fun ArrSeries.formatNextAiringTime(): String?
expect fun ArrMovie.formatReleaseDate(): String?