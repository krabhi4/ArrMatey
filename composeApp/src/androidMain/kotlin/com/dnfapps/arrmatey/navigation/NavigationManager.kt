package com.dnfapps.arrmatey.navigation

import com.dnfapps.arrmatey.compose.TabItem
import com.dnfapps.arrmatey.instances.model.InstanceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationManager(
    private val settingsNavigation: SettingsNavigation,
    private val seriesNavigation: SeriesTabNavigation,
    private val movieNavigation: MoviesTabNavigation
) {

    fun settings() = settingsNavigation

    fun arr(type: InstanceType) = when (type) {
        InstanceType.Sonarr -> seriesNavigation
        InstanceType.Radarr -> movieNavigation
    }

    fun series() = seriesNavigation

    fun movies() = movieNavigation

    private val _selectedTab = MutableStateFlow(TabItem.SHOWS)
    val selectedTab: StateFlow<TabItem> = _selectedTab.asStateFlow()

    fun setSelectedTab(tab: TabItem) {
        _selectedTab.value = tab
    }

}