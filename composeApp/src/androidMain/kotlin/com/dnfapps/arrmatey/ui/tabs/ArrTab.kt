package com.dnfapps.arrmatey.ui.tabs

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dnfapps.arrmatey.PreferencesStore
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.viewmodel.LibraryUiState
import com.dnfapps.arrmatey.api.arr.viewmodel.UiErrorType
import com.dnfapps.arrmatey.compose.components.MediaList
import com.dnfapps.arrmatey.compose.components.PosterGrid
import com.dnfapps.arrmatey.compose.utils.FilterBy
import com.dnfapps.arrmatey.compose.utils.SortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.compose.utils.applyFiltering
import com.dnfapps.arrmatey.compose.utils.applySorting
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.entensions.showSnackbarImmediately
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.navigation.RootNavigation
import com.dnfapps.arrmatey.navigation.RootScreen
import com.dnfapps.arrmatey.ui.components.FilterMenuButton
import com.dnfapps.arrmatey.ui.components.InstancePicker
import com.dnfapps.arrmatey.ui.components.SortMenuButton
import com.dnfapps.arrmatey.ui.components.ViewTypeMenuButton
import com.dnfapps.arrmatey.ui.theme.ViewType
import com.dnfapps.arrmatey.ui.viewmodel.ArrViewModel
import com.dnfapps.arrmatey.ui.viewmodel.ArrViewModelFactory
import com.dnfapps.arrmatey.ui.viewmodel.NetworkConnectivityViewModel
import com.dnfapps.arrmatey.ui.viewmodel.rememberInstanceFor
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArrTab(type: InstanceType) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val appNavigation = viewModel<RootNavigation>()

    val networkViewModel = viewModel<NetworkConnectivityViewModel>()
    val instance = rememberInstanceFor(type)

    val preferenceStore: PreferencesStore = koinInject()
    val selectedSortOrder by preferenceStore.sortOrder.collectAsState(SortOrder.Asc)
    val selectedSortOption by preferenceStore.sortBy.collectAsState(SortBy.Title)
    val selectedFilter by preferenceStore.filterBy.collectAsState(FilterBy.All)
    val viewTypeMap by preferenceStore.viewType.collectAsState(emptyMap())
    val selectedViewType = viewTypeMap[type] ?: ViewType.Grid

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
                        InstancePicker(type)

                        if (hasServerConnetivityError) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        val message = context.getString(R.string.instance_connect_error, instance?.url ?: "")
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
                        ViewTypeMenuButton(
                            viewType = selectedViewType,
                            onViewTypeChanged = { preferenceStore.saveViewType(type, it) }
                        )
                        FilterMenuButton(
                            instanceType = type,
                            selectedFilter = selectedFilter,
                            onFilterChange = {
                                preferenceStore.saveFilterBy(it)
                            }
                        )
                        SortMenuButton(
                            instanceType = type,
                            onSortChanged = {
                                preferenceStore.saveSortBy(it)
                            },
                            onOrderChanged = {
                                preferenceStore.saveSortOrder(it)
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
                val arrViewModel: ArrViewModel = viewModel(
                    key = instance.id.toString(),
                    factory = ArrViewModelFactory(instance)
                )

                val uiState by arrViewModel.uiState.collectAsStateWithLifecycle()
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
                                    arrViewModel.refreshLibrary()
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val items = state.items
                                .applyFiltering(type, selectedFilter)
                                .applySorting(type, selectedSortOption, selectedSortOrder)

                            if (items.isEmpty()) {
                                EmptyLibraryView(modifier = Modifier.align(Alignment.Center))
                            } else {
                                MediaView(
                                    items = items,
                                    onItemClick = {
                                        appNavigation.navigateTo(
                                            RootScreen.MediaDetails(
                                                type = type,
                                                id = it.id
                                            )
                                        )
                                    },
                                    viewType = selectedViewType
                                )
                            }
                        }
                    }

                    is LibraryUiState.Error -> {
                        LaunchedEffect(state.error) {
                            snackbarHostState.showSnackbarImmediately(state.error.message)
                        }

                        hasServerConnetivityError = state.type == UiErrorType.Network

                        var isRefreshing by remember { mutableStateOf(false) }

                        LaunchedEffect(state) {
                            isRefreshing = false
                        }

                        LaunchedEffect(isRefreshing) {
                            if (isRefreshing) {
                                scope.launch { arrViewModel.refreshLibrary() }
                            }
                        }

                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = {
                                isRefreshing = true
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            InstanceErrorView(
                                onRefresh = { isRefreshing = true },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            } ?: run {
                NoInstanceView(
                    type = type,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun NoInstanceView(
    type: InstanceType,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.CloudQueue,
            contentDescription = null,
            modifier = Modifier.size(128.dp)
        )
        Text(
            text = stringResource(R.string.no_type_instances, type.name),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        Text(text = stringResource(R.string.no_type_instances_message, type.name))
    }
}

@Composable
private fun EmptyLibraryView(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.VideoLibrary,
            contentDescription = null,
            modifier = Modifier.size(128.dp)
        )
        Text(
            text = stringResource(R.string.empty_library),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = stringResource(R.string.empty_library_message)
        )
    }
}

@Composable
private fun InstanceErrorView(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(128.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = stringResource(R.string.couldnt_connect),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        Text(text = stringResource(R.string.couldnt_connect_message))
        Button(onClick = onRefresh) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
fun MediaView(
    items: List<AnyArrMedia>,
    onItemClick: (AnyArrMedia) -> Unit,
    viewType: ViewType
) {
    when (viewType) {
        ViewType.List -> MediaList(
            items = items,
            onItemClick = onItemClick,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxSize()
        )
        ViewType.Grid -> PosterGrid(
            items = items,
            onItemClick = onItemClick,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}