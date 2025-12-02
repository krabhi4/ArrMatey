package com.dnfapps.arrmatey.ui.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.compose.components.FilterMenuButton
import com.dnfapps.arrmatey.compose.components.PosterGrid
import com.dnfapps.arrmatey.compose.components.SortMenuButton
import com.dnfapps.arrmatey.compose.utils.FilterBy
import com.dnfapps.arrmatey.compose.utils.SortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.compose.utils.applyFiltering
import com.dnfapps.arrmatey.compose.utils.applySorting
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.entensions.showSnackbarImmediately
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.ui.viewmodel.InstanceViewModel
import com.dnfapps.arrmatey.ui.viewmodel.LibraryUiError
import com.dnfapps.arrmatey.ui.viewmodel.LibraryUiState
import com.dnfapps.arrmatey.ui.viewmodel.NetworkConnectivityViewModel
import com.dnfapps.arrmatey.ui.viewmodel.rememberArrViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArrTab(type: InstanceType) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val networkViewModel = viewModel<NetworkConnectivityViewModel>()
    val instanceViewModel = viewModel<InstanceViewModel>()
    val instance by instanceViewModel.getFirstInstance(type).collectAsState(null)

    var selectedSortOption by remember { mutableStateOf(SortBy.Title) }
    var selectedSortOrder by remember { mutableStateOf(SortOrder.Asc) }
    var selectedFilter by remember { mutableStateOf(FilterBy.All) }

    val title = when (type) {
        InstanceType.Sonarr -> stringResource(R.string.series)
        InstanceType.Radarr -> stringResource(R.string.movies)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var hasServerConnetivityError by remember { mutableStateOf(false) }
    val hasNetworkConnection by networkViewModel.isConnected.collectAsStateWithLifecycle()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = title)
                        if (hasServerConnetivityError) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        var message = context.getString(R.string.instance_connect_error, instance?.url ?: "")
                                        if (instance?.cacheOnDisk == true) {
                                            message += ". ${context.getString(R.string.showing_cached_library)}"
                                        }
                                        snackbarHostState.showSnackbarImmediately(message = message)
                                    }
                                }
                            )
                        }
                        if (!hasNetworkConnection) {
                            Icon(
                                imageVector = Icons.Default.SignalWifiOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        snackbarHostState.showSnackbarImmediately(message = context.getString(R.string.no_network))
                                    }
                                }
                            )
                        }
                    }
                },
                actions = {
                    instance?.let {
                        FilterMenuButton(
                            instanceType = type,
                            selectedFilter = selectedFilter,
                            onFilterChange = { selectedFilter = it }
                        )
                        SortMenuButton(
                            instanceType = type,
                            onSortChanged = {
                                selectedSortOption = it
                            },
                            onOrderChanged = {
                                selectedSortOrder = it
                            },
                            sortBy = selectedSortOption,
                            sortOrder = selectedSortOrder
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues.copy(bottom = 0.dp))
                .fillMaxSize()
        ) {
            instance?.let { instance ->
                val viewModel = rememberArrViewModel(instance)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                when (val state = uiState) {
                    is LibraryUiState.Initial,
                    is LibraryUiState.Loading -> {
                        LoadingIndicator(
                            modifier = Modifier
                                .size(96.dp)
                                .align(Alignment.Center)
                        )
                    }

                    is LibraryUiState.Success -> {
                        PullToRefreshBox(
                            isRefreshing = state.isRefreshing,
                            onRefresh = {
                                scope.launch {
                                    viewModel.refreshLibrary()
                                }
                            }
                        ) {
                            PosterGrid(
                                items = state.items
                                    .applyFiltering(type, selectedFilter)
                                    .applySorting(type, selectedSortOption, selectedSortOrder),
                                onItemClick = {},
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    is LibraryUiState.Error -> {
                        LaunchedEffect(state.error) {
                            snackbarHostState.showSnackbarImmediately(state.error.message)
                        }

                        hasServerConnetivityError = state.type == LibraryUiError.Network

                        var isRefreshing by remember { mutableStateOf(false) }

                        LaunchedEffect(state) {
                            isRefreshing = false
                        }

                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = {
                                isRefreshing = true
                                scope.launch {
                                    viewModel.refreshLibrary()
                                }
                            }
                        ) {
                            if (state.cachedItems.isNotEmpty()) {
                                PosterGrid(
                                    items = state.cachedItems
                                        .applyFiltering(type, selectedFilter)
                                        .applySorting(type, selectedSortOption, selectedSortOrder),
                                    onItemClick = {},
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // todo - error screen
                                Text(text = "error occurred")
                            }
                        }
                    }
                }
            } ?: run {
                Text(text = "No instances found")
            }
        }
    }
}