package com.dnfapps.arrmatey.compose.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeUIViewController
import com.dnfapps.arrmatey.ui.theme.iOSTheme

fun MediaHeaderBannerViewController(
    bannerUrl: String?
) = ComposeUIViewController {
    iOSTheme {
        DetailHeaderBanner(bannerUrl)
    }
}