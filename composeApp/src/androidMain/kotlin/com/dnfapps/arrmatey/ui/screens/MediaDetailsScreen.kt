package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.arr.api.model.ArrMovie
import com.dnfapps.arrmatey.arr.api.model.ArrSeries
import com.dnfapps.arrmatey.arr.state.MediaDetailsUiState
import com.dnfapps.arrmatey.arr.viewmodel.ArrMediaDetailsViewModel
import com.dnfapps.arrmatey.di.koinInjectParams
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.entensions.headerBarColors
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.Navigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.ui.components.DetailsHeader
import com.dnfapps.arrmatey.ui.components.InfoArea
import com.dnfapps.arrmatey.ui.components.ItemDescriptionCard
import com.dnfapps.arrmatey.ui.components.MovieFileView
import com.dnfapps.arrmatey.ui.components.OverlayTopAppBar
import com.dnfapps.arrmatey.ui.components.SeasonsArea
import com.dnfapps.arrmatey.ui.components.UpcomingDateView
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaDetailsScreen(
    id: Long,
    type: InstanceType,
    mediaDetailsViewModel: ArrMediaDetailsViewModel = koinInjectParams(id, type),
    navigationManager: NavigationManager = koinInject(),
    navigation: Navigation<ArrScreen> = navigationManager.arr(type)
) {
    val navigation = navigationManager.arr(type)

    val uiState by mediaDetailsViewModel.uiState.collectAsStateWithLifecycle()
    val automaticSearchIds by mediaDetailsViewModel.automaticSearchIds.collectAsStateWithLifecycle()
    val lastSearchResult by mediaDetailsViewModel.lastSearchResult.collectAsStateWithLifecycle()

    val isMonitored by remember { derivedStateOf {
        (uiState as? MediaDetailsUiState.Success)?.item?.monitored ?: false
    } }

    val scrollState = rememberScrollState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues.copy(bottom = 0.dp, top = 0.dp))
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is MediaDetailsUiState.Initial,
                is MediaDetailsUiState.Loading -> {
                    LoadingIndicator(
                        modifier = Modifier
                            .size(96.dp)
                            .align(Alignment.Center)
                    )
                }
                is MediaDetailsUiState.Error -> {
                    Text(text = state.message ?: "")
                }
                is MediaDetailsUiState.Success -> {
                    val item = state.item
                    PullToRefreshBox(
                        isRefreshing = false,
                        onRefresh = { mediaDetailsViewModel.refreshDetails() }
                    ) {
                        Column(
                            modifier = Modifier.verticalScroll(scrollState),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DetailsHeader(item)

                            Column(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                UpcomingDateView(item)

                                item.overview?.let { overview ->
                                    ItemDescriptionCard(overview)
                                }

                                when (item) {
                                    is ArrSeries -> SeasonsArea(
                                        series = item,
                                        episodes = state.episodes,
                                        searchIds = automaticSearchIds,
                                        searchResult = lastSearchResult,
                                        onToggleSeasonMonitor = {
                                            mediaDetailsViewModel.toggleSeasonMonitored(it)
                                        },
                                        onToggleEpisodeMonitor = {
                                            mediaDetailsViewModel.toggleEpisodeMonitored(it)
                                        },
                                        onEpisodeAutomaticSearch = {
                                            mediaDetailsViewModel.performEpisodeAutomaticLookup(it)
                                        },
                                        onSeasonAutomaticSearch = {
                                            mediaDetailsViewModel.performSeasonAutomaticLookup(it)
                                        }
                                    )
                                    is ArrMovie -> MovieFileView(
                                        movie = item,
                                        movieExtraFiles = state.extraFiles,
                                        searchIds = automaticSearchIds,
                                        searchResult = lastSearchResult,
                                        onAutomaticSearch = {
                                            mediaDetailsViewModel.performMovieAutomaticLookup(it)
                                        }
                                    )
                                }

                                InfoArea(item)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
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
                        onClick = {
                            mediaDetailsViewModel.toggleMonitored()
                        },
                        colors = IconButtonDefaults.headerBarColors()
                    ) {
                        Icon(
                            imageVector = if (isMonitored) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}