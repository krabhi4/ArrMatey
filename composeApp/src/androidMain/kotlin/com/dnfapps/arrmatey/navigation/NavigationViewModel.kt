package com.dnfapps.arrmatey.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.dnfapps.arrmatey.compose.TabItem

class NavigationViewModel: ViewModel() {
    val rootBackStack = mutableStateListOf<RootScreen>(RootScreen.HomeScreen)
    val homeTabBackStack = mutableStateListOf<HomeTab>(HomeTab.SeriesTab)
    val settingsTabBackStack = mutableStateListOf<SettingsScreen>(SettingsScreen.Landing)

    fun navigateToHomeTab(tab: TabItem) {
        val navKey = when (tab) {
            TabItem.SHOWS -> HomeTab.SeriesTab
            TabItem.MOVIES -> HomeTab.MoviesTab
            TabItem.SETTINGS -> HomeTab.SettingsTab
        }
        homeTabBackStack.add(navKey)
    }

    fun navigateToSettingsScreen(screen: SettingsScreen) {
        settingsTabBackStack.add(screen)
    }
}