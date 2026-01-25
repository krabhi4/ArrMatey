package com.dnfapps.arrmatey.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.arr.api.client.LoggerLevel
import com.dnfapps.arrmatey.datastore.PreferencesStore
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.navigation.SettingsNavigation
import com.dnfapps.arrmatey.ui.components.DropdownPicker
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevSettingsScreen(
    preferenceStore: PreferencesStore = koinInject<PreferencesStore>(),
    settingsNav: SettingsNavigation = koinInject<SettingsNavigation>()
) {
    val showInfoCardMap by preferenceStore.showInfoCards.collectAsState(emptyMap())
    val activityPollingOn by preferenceStore.enableActivityPolling.collectAsState(true)
    val logLevel by preferenceStore.httpLogLevel.collectAsState(LoggerLevel.Headers)
    val useDynamicTheme by preferenceStore.useDynamicTheme.collectAsStateWithLifecycle(true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Developer Settings") },
                navigationIcon = {
                    IconButton(
                        onClick = { settingsNav.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { pv ->
        Box(modifier = Modifier.padding(pv)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                InstanceType.entries.forEach { instanceType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = showInfoCardMap[instanceType] ?: true,
                                onValueChange = { preferenceStore.setInfoCardVisibility(instanceType, it) }
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Show ${instanceType.name} info card"
                        )
                        Switch(
                            checked = showInfoCardMap[instanceType] ?: true,
                            onCheckedChange = null
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = activityPollingOn,
                            onValueChange = { preferenceStore.toggleActivityPolling() }
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable activity polling"
                    )
                    Switch(
                        checked = activityPollingOn,
                        onCheckedChange = null
                    )
                }

                DropdownPicker(
                    options = LoggerLevel.entries(),
                    selectedOption = logLevel,
                    onOptionSelected = { preferenceStore.setLogLevel(it) },
                    label = { Text("HTTP Logging Level") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = useDynamicTheme,
                                onValueChange = { preferenceStore.toggleUseDynamicTheme() }
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Use dynamic themeing"
                        )
                        Switch(
                            checked = useDynamicTheme,
                            onCheckedChange = null
                        )
                    }
                }
            }
        }
    }
}