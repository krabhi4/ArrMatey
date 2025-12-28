package com.dnfapps.arrmatey.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.dnfapps.arrmatey.compose.TabItem
import com.dnfapps.arrmatey.entensions.getString
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.ui.tabs.SettingsTabNavHost
import com.dnfapps.arrmatey.ui.tabs.ArrTab

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
}