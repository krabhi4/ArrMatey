package com.dnfapps.arrmatey.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dnfapps.arrmatey.compose.TabItem
import com.dnfapps.arrmatey.entensions.getString
import com.dnfapps.arrmatey.navigation.HomeScreenNavHost
import com.dnfapps.arrmatey.navigation.HomeTab
import com.dnfapps.arrmatey.navigation.NavigationViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun HomeScreen() {
    val navigationViewModel = viewModel<NavigationViewModel>()

    val selectedTab by remember { derivedStateOf {
        when (navigationViewModel.homeTabBackStack.lastOrNull()) {
            HomeTab.SeriesTab -> TabItem.SHOWS
            HomeTab.SettingsTab -> TabItem.SETTINGS
            else -> TabItem.SHOWS
        }
    }}

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            HomeScreenNavHost()
        }
        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
            TabItem.entries.forEach { entry ->
                NavigationBarItem(
                    selected = entry == selectedTab,
                    onClick = {
                        navigationViewModel.navigateToHomeTab(entry)
                    },
                    icon = {
                        Icon(
                            imageVector = entry.androidIcon,
                            contentDescription = getString(entry.textKey)
                        )
                    },
                    label = { Text(text = getString(entry.textKey)) }
                )
            }
        }
    }

//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        topBar = {},
//        bottomBar = {
//            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
//                TabItem.entries.forEach { entry ->
//                    NavigationBarItem(
//                        selected = entry == selectedTab,
//                        onClick = {
//                            navigationViewModel.navigateToHomeTab(entry)
//                        },
//                        icon = {
//                            Icon(
//                                imageVector = entry.androidIcon,
//                                contentDescription = null
//                            )
//                        },
//                        label = { Text(text = context.getString(entry.textKey)) }
//                    )
//                }
//            }
//        }
//    ) {
//        Box(modifier = Modifier.padding(it).fillMaxSize()) {
//            HomeScreenNavHost()
//        }
//    }
}