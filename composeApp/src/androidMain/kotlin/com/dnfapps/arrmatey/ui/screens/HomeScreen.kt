package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dnfapps.arrmatey.compose.TabItem
import com.dnfapps.arrmatey.entensions.getString

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {},
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                TabItem.entries.forEach { entry ->
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = {
                            Icon(
                                imageVector = entry.androidIcon,
                                contentDescription = null
                            )
                        },
                        label = { Text(text = context.getString(entry.textKey)) }
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {

        }
    }
}