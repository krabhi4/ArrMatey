package com.dnfapps.arrmatey.navigation

import androidx.navigation3.runtime.NavKey
import com.dnfapps.arrmatey.model.InstanceType

sealed interface RootScreen : NavKey {
    object HomeScreen: RootScreen
    data class MediaDetails(val type: InstanceType, val id: Int): RootScreen
}

sealed interface HomeTab : NavKey {
    object SeriesTab : HomeTab
    object MoviesTab: HomeTab
    object SettingsTab : HomeTab
}

sealed interface SettingsScreen : NavKey {
    object Landing : SettingsScreen
    object AddInstance : SettingsScreen
    class EditInstance(val id: Long): SettingsScreen
    object Dev: SettingsScreen
}