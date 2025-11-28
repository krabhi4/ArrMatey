package com.dnfapps.arrmatey.navigation

import androidx.navigation3.runtime.NavKey

sealed interface RootScreen : NavKey {
    object HomeScreen: RootScreen
}

sealed interface HomeTab : NavKey {
    object SeriesTab : HomeTab
    object SettingsTab : HomeTab
}

sealed interface SettingsScreen : NavKey {
    object Landing : SettingsScreen
    object AddInstance : SettingsScreen
}