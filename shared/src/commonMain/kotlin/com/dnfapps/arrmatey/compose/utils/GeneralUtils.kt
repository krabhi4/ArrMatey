package com.dnfapps.arrmatey.compose.utils

import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.pow

fun Long.bytesAsFileSizeString(): String {
    if (this < 1024L) return "$this B"

    val units = arrayOf("KB", "MB", "GB", "TB", "PB", "EB")
    val bytes = this.toDouble()
    val exp = (ln(bytes) / ln(1024.0)).toInt().coerceAtMost(units.lastIndex)
    val value = bytes / 1024.0.pow(exp)

    // Format to 1 decimal: 9.5 instead of 9.48274
    val decimal = (value * 10.0).let { floor(it) / 10.0 }

    return "${decimal.toInt()}.${(decimal * 10).toInt() % 10} ${units[max(exp-1,0)]}"
}
