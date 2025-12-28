package com.dnfapps.arrmatey.navigation

import androidx.navigation3.runtime.NavKey
import com.dnfapps.arrmatey.model.InstanceType

sealed interface HomeTab : NavKey {
    data object SeriesTab : HomeTab
    data object MoviesTab: HomeTab
    data object SettingsTab : HomeTab
}

sealed interface ArrScreen : NavKey {
    data object Library: ArrScreen
    data class Details(val type: InstanceType, val id: Int): ArrScreen
    data class Preview<T>(val item: T, val type: InstanceType): ArrScreen
    data class Search(val query: String = ""): ArrScreen
}

sealed interface SettingsScreen : NavKey {
    data object Landing : SettingsScreen
    data object AddInstance : SettingsScreen
    data class EditInstance(val id: Long): SettingsScreen
    data object Dev: SettingsScreen
}