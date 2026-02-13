package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.api.model.ArrMovie
import com.dnfapps.arrmatey.arr.api.model.ArrSeries
import com.dnfapps.arrmatey.arr.api.model.Arrtist
import com.dnfapps.arrmatey.arr.api.model.QualityProfile
import com.dnfapps.arrmatey.arr.api.model.RootFolder
import com.dnfapps.arrmatey.arr.api.model.Tag
import com.dnfapps.arrmatey.arr.state.MediaDetailsUiState
import com.dnfapps.arrmatey.arr.viewmodel.ArrMediaDetailsViewModel
import com.dnfapps.arrmatey.client.OperationStatus
import com.dnfapps.arrmatey.di.koinInjectParams
import com.dnfapps.arrmatey.entensions.SafeSnackbar
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.entensions.headerBarColors
import com.dnfapps.arrmatey.entensions.showErrorImmediately
import com.dnfapps.arrmatey.entensions.showSnackbarImmediately
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.Navigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.shared.MR
import com.dnfapps.arrmatey.ui.components.AlbumsArea
import com.dnfapps.arrmatey.ui.components.DetailsHeader
import com.dnfapps.arrmatey.ui.components.InfoArea
import com.dnfapps.arrmatey.ui.components.ItemDescriptionCard
import com.dnfapps.arrmatey.ui.components.LabelledSwitch
import com.dnfapps.arrmatey.ui.components.MovieFileView
import com.dnfapps.arrmatey.ui.components.OverlayTopAppBar
import com.dnfapps.arrmatey.ui.components.SeasonsArea
import com.dnfapps.arrmatey.ui.components.UpcomingDateView
import com.dnfapps.arrmatey.ui.sheets.EditArtistSheet
import com.dnfapps.arrmatey.ui.sheets.EditMovieSheet
import com.dnfapps.arrmatey.ui.sheets.EditSeriesSheet
import com.dnfapps.arrmatey.utils.mokoString
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
    val uiState by mediaDetailsViewModel.uiState.collectAsStateWithLifecycle()
    val automaticSearchIds by mediaDetailsViewModel.automaticSearchIds.collectAsStateWithLifecycle()
    val lastSearchResult by mediaDetailsViewModel.lastSearchResult.collectAsStateWithLifecycle()

    val isMonitored by mediaDetailsViewModel.isMonitored.collectAsStateWithLifecycle()
    val qualityProfiles by mediaDetailsViewModel.qualityProfiles.collectAsStateWithLifecycle()
    val rootFolders by mediaDetailsViewModel.rootFolders.collectAsStateWithLifecycle()
    val tags by mediaDetailsViewModel.tags.collectAsStateWithLifecycle()
    val deleteStatus by mediaDetailsViewModel.deleteStatus.collectAsStateWithLifecycle()

    val monitorStatus by mediaDetailsViewModel.monitorStatus.collectAsStateWithLifecycle()
    val seasonDeleteStatus by mediaDetailsViewModel.deleteSeasonStatus.collectAsStateWithLifecycle()
    val albumDeleteStatus by mediaDetailsViewModel.deleteAlbumStatus.collectAsStateWithLifecycle()
    val editStatus by mediaDetailsViewModel.editItemStatus.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(deleteStatus) {
        when (deleteStatus) {
            is OperationStatus.Success -> navigation.popBackStack()
            is OperationStatus.Error -> {}
            else -> {}
        }
    }

    val searchQueuedMessage = mokoString(MR.strings.search_queued)
    val searchErrorMessage = mokoString(MR.strings.search_error)
    LaunchedEffect(lastSearchResult) {
        when (lastSearchResult) {
            true -> {
                snackbarHostState.showSnackbarImmediately(searchQueuedMessage)
            }
            false -> {
                snackbarHostState.showErrorImmediately(searchErrorMessage)
            }
            else -> {}
        }
    }

    val scrollState = rememberScrollState()

    var confirmDelete by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var moveFilesItem by remember { mutableStateOf<ArrMedia?>(null) }

    LaunchedEffect(editStatus) {
        when (val status = editStatus) {
            is OperationStatus.Success -> {
                snackbarHostState.showSnackbarImmediately("Item edited successfully")
                showEditSheet = false
            }
            is OperationStatus.Error -> {
                snackbarHostState.showErrorImmediately("Error editing item")
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                SafeSnackbar(data)
            }
        }
    ) { paddingValues ->
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
                            DetailsHeader(item, type)

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
                                        },
                                        deleteSeasonFiles = { seasonNumber ->
                                            mediaDetailsViewModel.deleteSeasonFiles(seasonNumber)
                                        },
                                        seasonDeleteInProgress = seasonDeleteStatus is OperationStatus.InProgress
                                    )
                                    is ArrMovie -> MovieFileView(
                                        movie = item,
                                        movieExtraFiles = state.extraFiles,
                                        searchIds = automaticSearchIds,
                                        onAutomaticSearch = {
                                            mediaDetailsViewModel.performAutomaticLookup()
                                        }
                                    )
                                    is Arrtist -> AlbumsArea(
                                        artist = item,
                                        albums = state.albums,
                                        tracks = state.tracks,
                                        trackFiles = state.trackFiles,
                                        searchIds = automaticSearchIds,
                                        onToggleAlbumMonitor = {
                                            mediaDetailsViewModel.toggleAlbumMonitored(it)
                                        },
                                        onAlbumAutomaticSearch = {
                                            mediaDetailsViewModel.performAlbumAutomaticLookup(it)
                                        },
                                        deleteAlbumFiles = {
                                            mediaDetailsViewModel.deleteAlbumFiles(it)
                                        },
                                        albumDeleteInProgress = albumDeleteStatus is OperationStatus.InProgress,
                                    )
                                }

                                InfoArea(item, qualityProfiles, tags)
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
                            contentDescription = mokoString(MR.strings.back)
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
                    MenuButton(
                        onEdit = { showEditSheet = true },
                        onDelete = { confirmDelete = true },
                        onRefresh = {
                            mediaDetailsViewModel.performRefresh()
                        },
                        showSearch = type.includeTopLevelAutomaticSearchOption,
                        enableSearch = isMonitored,
                        onSearchMonitored = {
                            mediaDetailsViewModel.performAutomaticLookup()
                        }
                    )
                }
            )

            if (confirmDelete) {
                ConfirmDeleteAlert(
                    deleteInProgress = deleteStatus is OperationStatus.InProgress,
                    onDismiss = { confirmDelete = false },
                    onDelete = { deleteFiles, addExclusion ->
                        mediaDetailsViewModel.deleteMedia(deleteFiles, addExclusion)
                    }
                )
            }

            (uiState as? MediaDetailsUiState.Success)?.let { success ->
                if (showEditSheet) {
                    EditMediaSheet(
                        item = success.item,
                        qualityProfiles = qualityProfiles,
                        rootFolders = rootFolders,
                        tags = tags,
                        editInProgress = editStatus is OperationStatus.InProgress,
                        onEditItem = {
                            if (success.item.rootFolderPath != it.rootFolderPath) {
                                moveFilesItem = it
                            } else {
                                mediaDetailsViewModel.editItem(it)
                            }
                        },
                        onDismiss = { showEditSheet = false }
                    )
                }
            }

            moveFilesItem?.let { item ->
                AlertDialog(
                    onDismissRequest = { moveFilesItem = null },
                    title = {
                        Text("Move files to ${item.rootFolderPath}?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                mediaDetailsViewModel.editItem(item, moveFiles = true)
                                moveFilesItem = null
                            }
                        ) {
                            Text(mokoString(MR.strings.yes))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                mediaDetailsViewModel.editItem(item)
                                moveFilesItem = null
                            }
                        ) {
                            Text(mokoString(MR.strings.no))
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmDeleteAlert(
    deleteInProgress: Boolean,
    onDismiss: () -> Unit,
    onDelete: (Boolean, Boolean) -> Unit
) {
    var addExclusion by remember { mutableStateOf(false) }
    var deleteFiles by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            LabelledSwitch(
                label = mokoString(MR.strings.add_exclusion),
                sublabel = mokoString(MR.strings.add_exclusion_description),
                checked = addExclusion,
                onCheckedChange = { addExclusion = !addExclusion }
            )
            LabelledSwitch(
                label = mokoString(MR.strings.delete_files),
                sublabel = mokoString(MR.strings.delete_files_description),
                checked = deleteFiles,
                onCheckedChange = { deleteFiles = !deleteFiles }
            )
            Button(
                onClick = { onDelete(deleteFiles, addExclusion) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                enabled = deleteInProgress
            ) {
                if (deleteInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                    Text(text = mokoString(MR.strings.delete))
                }
            }
        }
    }
}

@Composable
private fun EditMediaSheet(
    item: ArrMedia,
    qualityProfiles: List<QualityProfile>,
    rootFolders: List<RootFolder>,
    tags: List<Tag>,
    editInProgress: Boolean,
    onEditItem: (ArrMedia) -> Unit,
    onDismiss: () -> Unit
) {
    when (item) {
        is ArrMovie -> EditMovieSheet(
            item = item,
            qualityProfiles = qualityProfiles,
            rootFolders = rootFolders,
            tags = tags,
            editInProgress = editInProgress,
            onEditItem = onEditItem,
            onDismiss = onDismiss,
        )
        is ArrSeries -> EditSeriesSheet(
            item = item,
            qualityProfiles = qualityProfiles,
            rootFolders = rootFolders,
            tags = tags,
            editInProgress = editInProgress,
            onEditItem = onEditItem,
            onDismiss = onDismiss
        )
        is Arrtist -> EditArtistSheet(
            item = item,
            qualityProfiles = qualityProfiles,
            rootFolders = rootFolders,
            tags = tags,
            editInProgress = editInProgress,
            onEditItem = onEditItem,
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MenuButton(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRefresh: () -> Unit,
    showSearch: Boolean,
    enableSearch: Boolean,
    onSearchMonitored: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box {
        IconButton(
            onClick = { showMenu = !showMenu },
            colors = IconButtonDefaults.headerBarColors()
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }

        DropdownMenuPopup(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShape(0, 2),
                interactionSource = interactionSource
            ) {
                DropdownMenuItem(
                    text = { Text(mokoString(MR.strings.refresh)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        showMenu = false
                        onRefresh()
                    }
                )
                if (showSearch) {
                    DropdownMenuItem(
                        text = { Text(mokoString(MR.strings.search_monitored)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        enabled = enableSearch,
                        onClick = {
                            showMenu = false
                            onSearchMonitored()
                        }
                    )
                }
            }
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShape(1, 2),
                interactionSource = interactionSource
            ) {
                DropdownMenuItem(
                    text = { Text(mokoString(MR.strings.edit)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        showMenu = false
                        onEdit()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = mokoString(MR.strings.delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        showMenu = false
                        onDelete()
                    }
                )
            }
        }
    }
}