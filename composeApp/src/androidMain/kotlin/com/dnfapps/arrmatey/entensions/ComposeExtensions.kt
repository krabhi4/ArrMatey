package com.dnfapps.arrmatey.entensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

fun PaddingValues.copy(
    start: Dp = this.calculateLeftPadding(LayoutDirection.Ltr),
    end: Dp = this.calculateRightPadding(LayoutDirection.Ltr),
    top: Dp = this.calculateTopPadding(),
    bottom: Dp = this.calculateBottomPadding()
) = PaddingValues(start = start, end = end, top = top, bottom = bottom)

suspend fun SnackbarHostState.showSnackbarImmediately(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
): SnackbarResult {
    currentSnackbarData?.dismiss()
    return showSnackbar(message, actionLabel, withDismissAction, duration)
}