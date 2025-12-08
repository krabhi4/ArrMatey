package com.dnfapps.arrmatey.extensions

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dnfapps.arrmatey.utils.screenDensity

fun Int.pxToDp(): Dp = (this / screenDensity).dp