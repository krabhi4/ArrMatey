package com.dnfapps.arrmatey.extensions

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dnfapps.arrmatey.utils.screenDensity

fun Int.pxToDp(): Dp = (this / screenDensity).dp

fun Int.formatAsRuntime(): String {
    val hours = this / 60
    val minutes = this % 60

    return buildString {
        if (hours > 0) append("$hours${if (hours == 1) "h" else "h"}")
        if (minutes > 0) {
            if (hours > 0) append(" ")
            append("$minutes${if (minutes == 1) "m" else "m"}")
        }
        if (hours == 0 && minutes == 0) append("0m")
    }
}