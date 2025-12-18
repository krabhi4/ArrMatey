package com.dnfapps.arrmatey.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dnfapps.arrmatey.ui.screens.AddInstanceScreen
import com.dnfapps.arrmatey.ui.screens.DevSettingsScreen
import com.dnfapps.arrmatey.ui.screens.EditInstanceScreen
import com.dnfapps.arrmatey.ui.tabs.SettingsTab

@Composable
fun SettingsTabNavHost() {
    val navigationViewModel = viewModel<SettingsNavigation>()

    NavDisplay(
        backStack = navigationViewModel.backStack,
        onBack = { navigationViewModel.popBackStack() },
        entryProvider = entryProvider {
            entry<SettingsScreen.Landing> { SettingsTab() }
            entry<SettingsScreen.AddInstance> { AddInstanceScreen() }
            entry<SettingsScreen.EditInstance> { EditInstanceScreen(it.id) }
            entry<SettingsScreen.Dev> { DevSettingsScreen() }
        }
    )
}