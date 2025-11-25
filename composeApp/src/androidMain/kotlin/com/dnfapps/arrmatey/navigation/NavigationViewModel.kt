package com.dnfapps.arrmatey.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class NavigationViewModel: ViewModel() {
    val rootBackStack = mutableStateListOf<RootScreen>(RootScreen.HomeScreen)

    val homeTabBackStack = mutableStateListOf<HomeTab>(HomeTab.SeriesTab)
}