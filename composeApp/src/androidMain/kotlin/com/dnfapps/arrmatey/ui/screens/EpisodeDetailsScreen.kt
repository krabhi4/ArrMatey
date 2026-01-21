package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.arr.api.model.ArrSeries
import com.dnfapps.arrmatey.arr.api.model.Episode
import com.dnfapps.arrmatey.arr.viewmodel.EpisodeDetailsViewModel
import com.dnfapps.arrmatey.client.NetworkResult
import com.dnfapps.arrmatey.client.OperationStatus
import com.dnfapps.arrmatey.di.koinInjectParams
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.entensions.headerBarColors
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.Navigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.ui.components.EpisodeDetailsHeader
import com.dnfapps.arrmatey.ui.components.FileCard
import com.dnfapps.arrmatey.ui.components.HistoryItemView
import com.dnfapps.arrmatey.ui.components.ItemDescriptionCard
import com.dnfapps.arrmatey.ui.components.OverlayTopAppBar
import com.dnfapps.arrmatey.ui.components.ReleaseDownloadButtons
import org.koin.compose.koinInject

@Composable
fun EpisodeDetailsScreen(
    series: ArrSeries,
    episode: Episode,
    viewModel: EpisodeDetailsViewModel = koinInjectParams(series.id, episode),
    navigationManager: NavigationManager = koinInject(),
    navigation: Navigation<ArrScreen> = navigationManager.series()
) {
    val navigation = navigationManager.series()
    val scrollState = rememberScrollState()

    val currentEpisode by viewModel.episode.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val monitorStatus by viewModel.monitorStatus.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar for monitor status changes
    LaunchedEffect(monitorStatus) {
        when (monitorStatus) {
            is OperationStatus.Success -> {
                snackbarHostState.showSnackbar(
                    (monitorStatus as OperationStatus.Success).message ?: "Updated"
                )
                viewModel.resetMonitorStatus()
            }
            is OperationStatus.Error -> {
                snackbarHostState.showSnackbar(
                    (monitorStatus as OperationStatus.Error).message ?: ""
                )
                viewModel.resetMonitorStatus()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues.copy(top = 0.dp, bottom = 0.dp))
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EpisodeDetailsHeader(currentEpisode, series)

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    currentEpisode.overview?.let { overview ->
                        ItemDescriptionCard(overview)
                    }

                    ReleaseDownloadButtons(
                        onInteractiveClicked = {
                            val destination = ArrScreen.SeriesRelease(episodeId = currentEpisode.id)
                            navigation.navigateTo(destination)
                        },
                        onAutomaticClicked = {
                            viewModel.executeAutomaticSearch()
                        },
                        automaticSearchEnabled = currentEpisode.monitored
                    )

                    Text(
                        text = stringResource(R.string.files),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                    currentEpisode.episodeFile?.let { file ->
                        FileCard(file)
                    }

                    when (val historyResult = history) {
                        is NetworkResult.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is NetworkResult.Success -> {
                            if (historyResult.data.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.no_history),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                historyResult.data.forEach { historyItem ->
                                    HistoryItemView(historyItem)
                                }
                            }
                        }
                        is NetworkResult.Error -> {}
                        else -> {}
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            OverlayTopAppBar(
                scrollState = scrollState,
                modifier = Modifier.align(Alignment.TopCenter),
                navigationIcon = {
                    IconButton(
                        onClick = { navigation.popBackStack() },
                        colors = IconButtonDefaults.headerBarColors()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleMonitor() },
                        colors = IconButtonDefaults.headerBarColors()
                    ) {
                        Icon(
                            imageVector = if (currentEpisode.monitored) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}