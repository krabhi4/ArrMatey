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
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.compose.TabItem
import com.dnfapps.arrmatey.entensions.BadgeContent
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.ui.tabs.ActivityTab
import com.dnfapps.arrmatey.ui.tabs.ArrTab
import com.dnfapps.arrmatey.ui.tabs.CalendarTab
import com.dnfapps.arrmatey.ui.tabs.SettingsTabNavHost
import com.dnfapps.arrmatey.utils.mokoString
import org.koin.compose.koinInject
import java.util.Calendar

@SuppressLint("UnrememberedMutableState")
@Composable
fun HomeScreen(
    navigationManager: NavigationManager = koinInject()
) {
    val selectedTab by navigationManager.selectedTab.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                TabItem.SHOWS -> ArrTab(InstanceType.Sonarr)
                TabItem.MOVIES -> ArrTab(InstanceType.Radarr)
                TabItem.ACTIVITY -> ActivityTab()
                TabItem.CALENDAR -> CalendarTab()
                TabItem.SETTINGS -> SettingsTabNavHost()
            }
        }
        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
            TabItem.entries.forEach { entry ->
                NavigationBarItem(
                    selected = entry == selectedTab,
                    onClick = {
                        navigationManager.setSelectedTab(entry)
                    },
                    icon = {
                        BadgedBox(
                            badge = { BadgeContent(tabItem = entry) }
                        ) {
                            Icon(
                                imageVector = entry.androidIcon,
                                contentDescription = mokoString(entry.resource)
                            )
                        }
                    },
                    label = { Text(text = mokoString(entry.resource)) }
                )
            }
        }
    }
}