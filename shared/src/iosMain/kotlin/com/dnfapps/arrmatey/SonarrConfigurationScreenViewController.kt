package com.dnfapps.arrmatey

import androidx.compose.ui.window.ComposeUIViewController
import com.dnfapps.arrmatey.compose.screens.SonarrConfigurationScreen
import com.dnfapps.arrmatey.ui.theme.BasicTheme

fun SonarrConfigurationScreenViewController() = ComposeUIViewController {
    BasicTheme {
        SonarrConfigurationScreen()
    }
}