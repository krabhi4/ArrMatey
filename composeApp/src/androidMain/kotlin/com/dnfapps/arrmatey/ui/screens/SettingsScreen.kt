package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.arr.viewmodel.MoreScreenViewModel
import com.dnfapps.arrmatey.entensions.getDrawableId
import com.dnfapps.arrmatey.entensions.openLink
import com.dnfapps.arrmatey.isDebug
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.navigation.SettingsNavigation
import com.dnfapps.arrmatey.navigation.SettingsScreen
import com.dnfapps.arrmatey.shared.MR
import com.dnfapps.arrmatey.ui.components.navigation.NavigationDrawerButton
import com.dnfapps.arrmatey.ui.components.settings.AboutCard
import com.dnfapps.arrmatey.utils.MokoStrings
import com.dnfapps.arrmatey.utils.mokoString
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.util.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MoreScreenViewModel = koinInject(),
    navigationManager: NavigationManager = koinInject(),
    settingsNav: SettingsNavigation = navigationManager.settings(),
    moko: MokoStrings = koinInject()
) {
    val context = LocalContext.current
    val allInstances by viewModel.instances.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var showLibrariesSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = mokoString(MR.strings.settings)) },
                navigationIcon = {
                    NavigationDrawerButton(returnToHome = true)
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = mokoString(MR.strings.instances),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        allInstances.forEach { instance ->
                            Card(
                                shape = MaterialTheme.shapes.extraLarge,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    settingsNav.navigateTo(SettingsScreen.EditInstance(instance.id))
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Image(
                                        painter = painterResource(getDrawableId(instance.type.iconKey)),
                                        contentDescription = instance.type.name,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = instance.label,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = instance.url,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Card(
                            shape = MaterialTheme.shapes.extraLarge,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                settingsNav.navigateTo(SettingsScreen.AddInstance())
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircleOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = mokoString(MR.strings.add_instance),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                AboutCard(
                    onGitHubClick = {
                        context.openLink(moko.getString(MR.strings.app_link))
                    },
                    onDonateClick = {
                        context.openLink(moko.getString(MR.strings.bmac_link))
                    },
                    onLibrariesClick = { showLibrariesSheet = true },
                    modifier = Modifier.padding(top = 12.dp)
                )

                if (isDebug()) {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            settingsNav.navigateTo(SettingsScreen.Dev)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "Development Settings",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showLibrariesSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLibrariesSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
            ) {
                val libraries by produceState<Libs?>(null) {
                    value = withContext(Dispatchers.IO) {
                        Libs.Builder().withContext(context).build()
                    }
                }
                LibrariesContainer(
                    libraries = libraries,
                    modifier = Modifier.fillMaxSize(),
                    colors = LibraryDefaults.libraryColors(
                        libraryBackgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    padding = LibraryDefaults.libraryPadding(
                        licenseDialogContentPadding = 16.dp
                    ),
                    header = { item {
                      Text(
                          text = mokoString(MR.strings.libraries),
                          style = MaterialTheme.typography.headlineMedium,
                          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                      )
                    } }
                )
            }
        }
    }
}