package com.dnfapps.arrmatey

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.window.ComposeUIViewController
import com.dnfapps.arrmatey.compose.screens.ArrConfigurationScreen
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.ui.theme.BasicTheme

fun SonarrConfigurationScreenViewController() = ComposeUIViewController {
    BasicTheme {
        Column {  }
//        ArrConfigurationScreen(InstanceType.Sonarr)
    }
}