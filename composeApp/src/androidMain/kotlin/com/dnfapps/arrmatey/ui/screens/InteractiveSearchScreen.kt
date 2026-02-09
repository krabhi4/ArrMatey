package com.dnfapps.arrmatey.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.arr.api.model.ArrRelease
import com.dnfapps.arrmatey.arr.api.model.CustomFormat
import com.dnfapps.arrmatey.arr.api.model.Language
import com.dnfapps.arrmatey.arr.api.model.QualityInfo
import com.dnfapps.arrmatey.arr.api.model.ReleaseParams
import com.dnfapps.arrmatey.arr.api.model.ReleaseProtocol
import com.dnfapps.arrmatey.arr.state.DownloadState
import com.dnfapps.arrmatey.arr.state.ReleaseLibrary
import com.dnfapps.arrmatey.arr.viewmodel.InteractiveSearchViewModel
import com.dnfapps.arrmatey.compose.components.ProgressBox
import com.dnfapps.arrmatey.compose.utils.ReleaseFilterBy
import com.dnfapps.arrmatey.compose.utils.ReleaseSortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.compose.utils.singleLanguageLabel
import com.dnfapps.arrmatey.di.koinInjectParams
import com.dnfapps.arrmatey.entensions.Bullet
import com.dnfapps.arrmatey.entensions.SafeSnackbar
import com.dnfapps.arrmatey.entensions.bullet
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.entensions.showErrorImmediately
import com.dnfapps.arrmatey.entensions.showSnackbarImmediately
import com.dnfapps.arrmatey.extensions.formatAgeMinutes
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.Navigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.shared.MR
import com.dnfapps.arrmatey.ui.components.DropdownPicker
import com.dnfapps.arrmatey.utils.mokoString
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InteractiveSearchScreen(
    instanceType: InstanceType,
    releaseParams: ReleaseParams,
    canFilter: Boolean,
    defaultFilter: ReleaseFilterBy = ReleaseFilterBy.Any,
    viewModel: InteractiveSearchViewModel = koinInjectParams(instanceType, defaultFilter),
    navigationManager: NavigationManager = koinInject(),
    navigation: Navigation<ArrScreen> = navigationManager.arr(instanceType)
) {
    val releaseUiState by viewModel.releaseUiState.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadReleaseState.collectAsStateWithLifecycle()
    val downloadStatus by viewModel.downloadStatus.collectAsStateWithLifecycle()
    val filterState by viewModel.filterUiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var confirmRelease by remember { mutableStateOf<ArrRelease?>( null) }
    var showSearch by remember { mutableStateOf(false) }

    val downloadQueueSuccessMessage = mokoString(MR.strings.download_queue_success)
    val downloadQueueErrorMessage = mokoString(MR.strings.download_queue_error)

    var showFilterSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(releaseParams) {
        viewModel.getRelease(releaseParams)
    }

    LaunchedEffect(downloadStatus) {
        when (downloadStatus) {
            true -> snackbarHostState.showSnackbarImmediately(downloadQueueSuccessMessage)
            false -> snackbarHostState.showErrorImmediately(downloadQueueErrorMessage)
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                SafeSnackbar(data)
            }
        },
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
                    IconButton(
                        onClick = {
                            showSearch = !showSearch
                            if (!showSearch) viewModel.updateSearchQuery("")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = mokoString(MR.strings.search)
                        )
                    }
                    IconButton(
                        onClick = { showFilterSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = mokoString(MR.strings.filter)
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues.copy(bottom = 0.dp))
                .fillMaxSize()
        ) {
            when (val state = releaseUiState) {
                is ReleaseLibrary.Loading -> {
                    LoadingIndicator(
                        modifier = Modifier
                            .size(96.dp)
                            .align(Alignment.Center)
                    )
                }
                is ReleaseLibrary.Success -> {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 18.dp)
                    ) {
                        AnimatedVisibility(
                            visible = showSearch,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.clickable {
                                            viewModel.updateSearchQuery("")
                                            showSearch = false
                                        }
                                    )
                                },
                                placeholder = { Text(mokoString(MR.strings.search)) },
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            items(state.items) { item ->
                                val shouldAnimate =
                                    (downloadState as? DownloadState.Loading)?.guid == item.guid
                                ReleaseItem(
                                    item = item,
                                    onItemClick = {
                                        if (item.downloadAllowed) {
                                            viewModel.downloadRelease(item)
                                        } else {
                                            confirmRelease = item
                                        }
                                    },
                                    animate = shouldAnimate
                                )
                            }
                            if (state.items.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No results found"
                                        )
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(0.dp))
                            }
                        }
                    }
                }
                is ReleaseLibrary.Error -> {
                    Text(state.message)
                }
                else -> {}
            }

            confirmRelease?.let { release ->
                AlertDialog(
                    onDismissRequest = {
                        confirmRelease = null
                    },
                    title = {
                        Text(mokoString(MR.strings.grab_release_title))
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(mokoString(MR.strings.grab_release_message))
                            ReleaseItem(release)
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.downloadRelease(release, force = true)
                                confirmRelease = null
                            }
                        ) {
                            Text(mokoString(MR.strings.grab))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick =  {
                                confirmRelease = null
                            }
                        ) {
                            Text(mokoString(MR.strings.cancel))
                        }
                    }
                )
            }
        }

        if (showFilterSheet) {
            FilterSheet(
                canFilter = canFilter,
                onDismiss = { showFilterSheet = false },
                selectedSortBy = filterState.sortBy,
                onSortByChanged = { viewModel.setSortBy(it) },
                selectedSortOrder = filterState.sortOrder,
                onSortOrderChanged = { viewModel.setSortOrder(it) },
                selectedFilter = filterState.filterBy,
                onFilterChanged = { viewModel.setFilterBy(it) },
                libraryState = (releaseUiState as? ReleaseLibrary.Success),
                filterLanguage = filterState.language,
                onLanguageChange = { viewModel.setFilterLanguage(it) },
                filterCustomFormat = filterState.customFormat,
                onCustomFormatChange = { viewModel.setFilterCustomFormat(it) },
                filterQualityInfo = filterState.quality,
                onQualityChange = { viewModel.setFilterQuality(it) },
                filterIndexer = filterState.indexer,
                onIndexerChange = { viewModel.setFilterIndexer(it) },
                filterProtocol = filterState.protocol,
                onProtocolChange = { viewModel.setFilterProtocol(it) }
            )
        }
    }
}

@Composable
fun <T: ArrRelease> ReleaseItem(
    item: T,
    onItemClick: ((T) -> Unit)? = null,
    animate: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onItemClick?.invoke(item) },
                enabled = onItemClick != null
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        ProgressBox(
            animate = animate
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val secondLine = buildAnnotatedString {
                    withStyle(SpanStyle(color = item.peerColor)) {
                        append(item.typeLabel)
                    }
                    bullet()
                    append(item.quality.qualityLabel)
                    bullet()
                    append(item.size.bytesAsFileSizeString())
                }
                Text(
                    text = secondLine,
                    maxLines = 1
                )

                val thirdLine = listOf(
                    item.languages.singleLanguageLabel(),
                    item.indexerLabel,
                    item.ageMinutes.formatAgeMinutes()
                ).joinToString(Bullet)
                Text(
                    text = thirdLine,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    canFilter: Boolean,
    onDismiss: () -> Unit,
    selectedFilter: ReleaseFilterBy,
    onFilterChanged: (ReleaseFilterBy) -> Unit,
    selectedSortOrder: SortOrder,
    onSortOrderChanged: (SortOrder) -> Unit,
    selectedSortBy: ReleaseSortBy,
    onSortByChanged: (ReleaseSortBy) -> Unit,
    libraryState: ReleaseLibrary.Success?,
    filterLanguage: Language?,
    onLanguageChange: (Language?) -> Unit,
    filterCustomFormat: CustomFormat?,
    onCustomFormatChange: (CustomFormat?) -> Unit,
    filterQualityInfo: QualityInfo?,
    onQualityChange: (QualityInfo?) -> Unit,
    filterIndexer: String?,
    onIndexerChange: (String?) -> Unit,
    filterProtocol: ReleaseProtocol?,
    onProtocolChange: (ReleaseProtocol?) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            item {
                DropdownPicker(
                    options = ReleaseSortBy.entries,
                    selectedOption = selectedSortBy,
                    onOptionSelected = onSortByChanged,
                    label = { Text(mokoString(MR.strings.sort_by)) },
                    getOptionLabel = { mokoString(it.resource) }
                )
            }
            item {
                DropdownPicker(
                    options = SortOrder.entries,
                    selectedOption = selectedSortOrder,
                    onOptionSelected = onSortOrderChanged,
                    label = { Text(mokoString(MR.strings.sort_order)) },
                    getOptionLabel = { mokoString(it.resource) },
                    getOptionIcon = { it.androidIcon }
                )
            }

            if (canFilter) {
                item(span = { GridItemSpan(maxLineSpan)} ) {
                    DropdownPicker(
                        options = ReleaseFilterBy.entries,
                        selectedOption = selectedFilter,
                        onOptionSelected = onFilterChanged,
                        label = { Text(mokoString(MR.strings.filter_by)) },
                        getOptionLabel = { mokoString(it.resource) }
                    )
                }
            }

            libraryState?.filterQualities?.takeUnless { it.size < 2 }?.let { qualities ->
                item {
                    DropdownPicker(
                        options = qualities,
                        selectedOption = filterQualityInfo,
                        onOptionSelected = onQualityChange,
                        label = { Text(mokoString(MR.strings.quality))},
                        getOptionLabel = { it.qualityLabel },
                        includeAllOption = true,
                        allLabel = mokoString(MR.strings.any),
                        onAllSelected = { onQualityChange(null) }
                    )
                }
            }
            libraryState?.filterLanguages?.takeUnless { it.size < 2 }?.let { languages ->
                item {
                    DropdownPicker(
                        options = languages,
                        selectedOption = filterLanguage,
                        onOptionSelected = onLanguageChange,
                        label = { Text(mokoString(MR.strings.language)) },
                        getOptionLabel = { it.name ?: mokoString(MR.strings.unknown) },
                        includeAllOption = true,
                        allLabel = mokoString(MR.strings.any),
                        onAllSelected = { onLanguageChange(null) }
                    )
                }
            }
            libraryState?.filterCustomFormats?.takeUnless { it.size < 2 }?.let { customFormats ->
                item {
                    DropdownPicker(
                        options = customFormats,
                        selectedOption = filterCustomFormat,
                        onOptionSelected = onCustomFormatChange,
                        label = { Text(mokoString(MR.strings.custom_format)) },
                        getOptionLabel = { it.name },
                        includeAllOption = true,
                        allLabel = mokoString(MR.strings.any),
                        onAllSelected = { onLanguageChange(null) }
                    )
                }
            }
            libraryState?.filterProtocols?.takeUnless { it.size < 2 }?.let { protocols ->
                item {
                    DropdownPicker(
                        options = protocols,
                        selectedOption = filterProtocol,
                        onOptionSelected = onProtocolChange,
                        label = { Text(mokoString(MR.strings.protocol)) },
                        getOptionLabel = { it.name },
                        includeAllOption = true,
                        allLabel = mokoString(MR.strings.any),
                        onAllSelected = { onProtocolChange(null) }
                    )
                }
            }
            libraryState?.filterIndexers?.takeUnless { it.size < 2 }?.let { indexers ->
                item {
                    DropdownPicker(
                        options = indexers,
                        selectedOption = filterIndexer,
                        onOptionSelected = onIndexerChange,
                        label = { Text(mokoString(MR.strings.indexer)) },
                        includeAllOption = true,
                        allLabel = mokoString(MR.strings.any),
                        onAllSelected = { onIndexerChange(null) }
                    )
                }
            }
        }
    }
}