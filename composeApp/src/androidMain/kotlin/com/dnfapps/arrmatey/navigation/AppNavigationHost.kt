package com.dnfapps.arrmatey.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.dnfapps.arrmatey.ui.screens.HomeScreen

@Composable
fun AppNavHost() {
    val navigationViewModel = viewModel<NavigationViewModel>()
    val backStack = remember { navigationViewModel.rootBackStack }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                RootScreen.HomeScreen -> NavEntry(key) { HomeScreen() }
            }
        }
    )
}