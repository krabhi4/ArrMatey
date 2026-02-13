package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.arr.state.ArrLibrary
import com.dnfapps.arrmatey.arr.viewmodel.ActivityQueueViewModel
import com.dnfapps.arrmatey.arr.viewmodel.ArrSearchViewModel
import com.dnfapps.arrmatey.di.koinInjectParams
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.Navigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.shared.MR
import com.dnfapps.arrmatey.ui.components.MediaList
import com.dnfapps.arrmatey.ui.menu.SearchSortMenu
import com.dnfapps.arrmatey.utils.mokoString
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun ArrSearchScreen(
    initialQuery: String,
    type: InstanceType,
    viewModel: ArrSearchViewModel = koinInjectParams(type),
    activityQueueViewModel: ActivityQueueViewModel = koinInject(),
    navigationManager: NavigationManager = koinInject(),
    navigation: Navigation<ArrScreen> = navigationManager.arr(type)
) {

    var searchQuery by rememberSaveable { mutableStateOf(initialQuery) }

    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()

    val lookupState by viewModel.lookupUiState.collectAsStateWithLifecycle()
    val queueItems by activityQueueViewModel.queueItems.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        snapshotFlow { searchQuery }
            .debounce(500)
            .distinctUntilChanged()
            .collect { query ->
                viewModel.performLookup(query)
            }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.clearLookup() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { navigation.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = mokoString(MR.strings.back)
                        )
                    }
                },
                title = {},
                actions = {
                    SearchSortMenu(
                        sortBy = sortBy,
                        onSortChanged = { viewModel.setSortBy(it) },
                        sortOrder = sortOrder,
                        onOrderChanged = { viewModel.setSortOrder(it) }
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.clickable { searchQuery = "" }
                        )
                    },
                    placeholder = { Text(mokoString(MR.strings.search)) },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                ) {
                    when (val state = lookupState) {
                        is ArrLibrary.Initial -> {}
                        is ArrLibrary.Loading -> {
                            LoadingIndicator(
                                modifier = Modifier
                                    .size(96.dp)
                                    .align(Alignment.Center)
                            )
                        }

                        is ArrLibrary.Success -> {
                            if (state.items.isEmpty()) {
                                Text(
                                    text = mokoString(MR.strings.empty_library),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else {
                                MediaList(
                                    items = state.items,
                                    onItemClick = { item ->
                                        val destination = if (item.id == null) {
                                            ArrScreen.Preview(item)
                                        } else {
                                            ArrScreen.Details(item.id!!)
                                        }
                                        navigation.navigateTo(destination)
                                    },
                                    itemIsActive = { item -> queueItems.any { it.mediaId == item.id } },
                                )
                            }
                        }

                        is ArrLibrary.Error -> {
                            Text("An error occurred")
                        }
                    }
                }
            }
        }
    }
}