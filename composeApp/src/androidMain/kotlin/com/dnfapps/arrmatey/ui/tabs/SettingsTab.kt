package com.dnfapps.arrmatey.ui.tabs

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.navigation.SettingsNavigation
import com.dnfapps.arrmatey.navigation.SettingsScreen
import com.dnfapps.arrmatey.ui.screens.AddInstanceScreen
import com.dnfapps.arrmatey.ui.screens.DevSettingsScreen
import com.dnfapps.arrmatey.ui.screens.EditInstanceScreen
import com.dnfapps.arrmatey.ui.screens.SettingsScreen
import org.koin.compose.koinInject

@Composable
fun SettingsTabNavHost(
    navigationManager: NavigationManager = koinInject(),
    navigation: SettingsNavigation = navigationManager.settings()
) {
    NavDisplay(
        backStack = navigation.backStack,
        onBack = { navigation.popBackStack() },
        entryProvider = entryProvider {
            entry<SettingsScreen.Landing> { SettingsScreen() }
            entry<SettingsScreen.AddInstance> { AddInstanceScreen(it.type) }
            entry<SettingsScreen.EditInstance> { EditInstanceScreen(it.id) }
            entry<SettingsScreen.Dev> { DevSettingsScreen() }
        }
    )
}