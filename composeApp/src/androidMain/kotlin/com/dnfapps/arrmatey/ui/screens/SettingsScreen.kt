package com.dnfapps.arrmatey.ui.screens

import com.dnfapps.arrmatey.shared.MR
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.arr.viewmodel.MoreScreenViewModel
import com.dnfapps.arrmatey.entensions.getDrawableId
import com.dnfapps.arrmatey.isDebug
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.navigation.SettingsNavigation
import com.dnfapps.arrmatey.navigation.SettingsScreen
import com.dnfapps.arrmatey.ui.components.navigation.NavigationDrawerButton
import com.dnfapps.arrmatey.utils.mokoString
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MoreScreenViewModel = koinInject(),
    navigationManager: NavigationManager = koinInject(),
    settingsNav: SettingsNavigation = navigationManager.settings()
) {
    val allInstances by viewModel.instances.collectAsStateWithLifecycle()

    val radius = 12.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = mokoString(MR.strings.settings)) },
                navigationIcon = {
                    NavigationDrawerButton(returnToHome = true)
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier.padding(all = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = mokoString(MR.strings.instances), fontSize = 16.sp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    allInstances.forEachIndexed { index, instance ->
                        val topR = if (index == 0) radius else 0.dp
                        Card(
                            shape = RoundedCornerShape(topStart = topR, topEnd = topR),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                settingsNav.navigateTo(SettingsScreen.EditInstance(instance.id))
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .padding(start = 24.dp, end = 18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                Image(
                                    painter = painterResource(getDrawableId(instance.type.iconKey)),
                                    contentDescription = instance.type.name,
                                    modifier = Modifier.size(32.dp)
                                )
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(1.dp)
                                ) {
                                    Text(text = instance.label, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                                    Text(text = instance.url, fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    val topR = if (allInstances.isEmpty()) radius else 0.dp
                    Card(
                        shape = RoundedCornerShape(bottomEnd = radius, bottomStart = radius, topEnd = topR, topStart = topR),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            settingsNav.navigateTo(SettingsScreen.AddInstance())
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(text = mokoString(MR.strings.add_instance), fontSize = 18.sp, fontWeight = FontWeight.Normal)
                        }
                    }
                }

                if (isDebug()) {
                    Card(
                        shape = RoundedCornerShape(radius),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        onClick = {
                            settingsNav.navigateTo(SettingsScreen.Dev)
                        }
                    ) {
                        Text(text = "Development Settings", modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp))
                    }
                }
            }
        }
    }
}