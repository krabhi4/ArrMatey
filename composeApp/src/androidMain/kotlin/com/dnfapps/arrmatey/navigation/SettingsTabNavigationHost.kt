package com.dnfapps.arrmatey.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.dnfapps.arrmatey.ui.screens.AddInstanceScreen
import com.dnfapps.arrmatey.ui.tabs.SettingsTab

@Composable
fun SettingsTabNavHost() {
    val navigationViewModel = viewModel<NavigationViewModel>()
    val backStack = remember { navigationViewModel.settingsTabBackStack }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                SettingsScreen.Landing -> NavEntry(key) { SettingsTab() }
                SettingsScreen.AddInstance -> NavEntry(key) { AddInstanceScreen() }
            }
        }
    )
}