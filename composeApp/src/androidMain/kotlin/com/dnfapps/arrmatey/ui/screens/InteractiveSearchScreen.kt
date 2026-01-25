package com.dnfapps.arrmatey.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.arr.api.model.ArrRelease
import com.dnfapps.arrmatey.arr.api.model.ReleaseParams
import com.dnfapps.arrmatey.arr.state.DownloadState
import com.dnfapps.arrmatey.arr.state.LibraryUiState
import com.dnfapps.arrmatey.arr.viewmodel.InteractiveSearchViewModel
import com.dnfapps.arrmatey.compose.components.ProgressBox
import com.dnfapps.arrmatey.compose.utils.ReleaseFilterBy
import com.dnfapps.arrmatey.compose.utils.ReleaseSortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.compose.utils.singleLanguageLabel
import com.dnfapps.arrmatey.di.koinInjectParams
import com.dnfapps.arrmatey.entensions.Bullet
import com.dnfapps.arrmatey.entensions.bullet
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.entensions.getString
import com.dnfapps.arrmatey.entensions.stringResource
import com.dnfapps.arrmatey.extensions.formatAgeMinutes
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.Navigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.ui.components.DropdownPicker
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
    val context = LocalContext.current

    val releaseUiState by viewModel.releaseUiState.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadReleaseState.collectAsStateWithLifecycle()
    val downloadStatus by viewModel.downloadStatus.collectAsStateWithLifecycle()
    val filterState by viewModel.filterUiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var confirmRelease by remember { mutableStateOf<ArrRelease?>( null) }
    var showSearch by remember { mutableStateOf(false) }

    val downloadQueueSuccessMessage = stringResource(R.string.download_queue_success)
    val downloadQueueErrorMessage = stringResource(R.string.download_queue_error)

    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(releaseParams) {
        viewModel.getRelease(releaseParams)
    }

    LaunchedEffect(downloadStatus) {
        when(downloadStatus) {
            true -> downloadQueueSuccessMessage
            false -> downloadQueueErrorMessage
            else -> null
        }?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
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
                            contentDescription = stringResource(R.string.back)
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
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                    IconButton(
                        onClick = { showFilterSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.filter)
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
                is LibraryUiState.Loading -> {
                    LoadingIndicator(
                        modifier = Modifier
                            .size(96.dp)
                            .align(Alignment.Center)
                    )
                }
                is LibraryUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 18.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        item {
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
                                    placeholder = { Text(stringResource(R.string.search)) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                        items(state.items) { item ->
                            val shouldAnimate = (downloadState as? DownloadState.Loading)?.guid == item.guid
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
                        item {
                            Spacer(modifier = Modifier.height(0.dp))
                        }
                    }
                }
                is LibraryUiState.Error -> {
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
                        Text(stringResource(R.string.grab_release_title))
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(stringResource(R.string.grab_release_message))
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
                            Text(stringResource(R.string.grab))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick =  {
                                confirmRelease = null
                            }
                        ) {
                            Text(stringResource(R.string.cancel))
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
                onFilterChanged = { viewModel.setFilterBy(it) }
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
    onSortByChanged: (ReleaseSortBy) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            if (canFilter) {
                DropdownPicker(
                    options = ReleaseFilterBy.entries,
                    selectedOption = selectedFilter,
                    onOptionSelected = onFilterChanged,
                    label = { Text(stringResource(R.string.filter_by)) },
                    getOptionLabel = { stringResource(it.stringResource()) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DropdownPicker(
                    options = ReleaseSortBy.entries,
                    selectedOption = selectedSortBy,
                    onOptionSelected = onSortByChanged,
                    label = { Text(stringResource(R.string.sort_by)) },
                    getOptionLabel = { stringResource(it.stringResource()) },
                    modifier = Modifier.weight(1f)
                )
                DropdownPicker(
                    options = SortOrder.entries,
                    selectedOption = selectedSortOrder,
                    onOptionSelected = onSortOrderChanged,
                    label = { Text(stringResource(R.string.sort_order)) },
                    getOptionLabel = { getString(it.iosText) },
                    getOptionIcon = { it.androidIcon },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}