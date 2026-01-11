package com.dnfapps.arrmatey.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.api.client.ActivityQueue
import com.dnfapps.arrmatey.compose.TabItem
import com.dnfapps.arrmatey.entensions.BadgeContent
import com.dnfapps.arrmatey.entensions.stringResource
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.ui.tabs.ActivityTab
import com.dnfapps.arrmatey.ui.tabs.ArrTab
import com.dnfapps.arrmatey.ui.tabs.SettingsTabNavHost

@SuppressLint("UnrememberedMutableState")
@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(TabItem.SHOWS) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                TabItem.SHOWS -> ArrTab(InstanceType.Sonarr)
                TabItem.MOVIES -> ArrTab(InstanceType.Radarr)
                TabItem.ACTIVITY -> ActivityTab()
                TabItem.SETTINGS -> SettingsTabNavHost()
            }
        }
        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
            TabItem.entries.forEach { entry ->
                NavigationBarItem(
                    selected = entry == selectedTab,
                    onClick = {
                        selectedTab = entry
                    },
                    icon = {
                        BadgedBox(
                            badge = { BadgeContent(tabItem = entry) }
                        ) {
                            Icon(
                                imageVector = entry.androidIcon,
                                contentDescription = stringResource(entry.stringResource())
                            )
                        }
                    },
                    label = { Text(text = stringResource(entry.stringResource())) }
                )
            }
        }
    }
}