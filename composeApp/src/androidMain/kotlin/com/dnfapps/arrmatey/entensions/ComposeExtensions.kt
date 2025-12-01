package com.dnfapps.arrmatey.entensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

fun PaddingValues.copy(
    start: Dp = this.calculateLeftPadding(LayoutDirection.Ltr),
    end: Dp = this.calculateRightPadding(LayoutDirection.Ltr),
    top: Dp = this.calculateTopPadding(),
    bottom: Dp = this.calculateBottomPadding()
) = PaddingValues(start = start, end = end, top = top, bottom = bottom)