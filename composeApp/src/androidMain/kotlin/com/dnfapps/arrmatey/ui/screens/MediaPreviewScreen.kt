package com.dnfapps.arrmatey.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.api.model.ArrMovie
import com.dnfapps.arrmatey.arr.api.model.ArrSeries
import com.dnfapps.arrmatey.arr.api.model.MediaStatus
import com.dnfapps.arrmatey.arr.api.model.QualityProfile
import com.dnfapps.arrmatey.arr.api.model.RootFolder
import com.dnfapps.arrmatey.arr.api.model.SeriesMonitorType
import com.dnfapps.arrmatey.arr.api.model.SeriesType
import com.dnfapps.arrmatey.arr.api.model.Tag
import com.dnfapps.arrmatey.arr.viewmodel.MediaPreviewViewModel
import com.dnfapps.arrmatey.client.OperationStatus
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.di.koinInjectParams
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.entensions.headerBarColors
import com.dnfapps.arrmatey.entensions.stringResource
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.Navigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.ui.components.DetailsHeader
import com.dnfapps.arrmatey.ui.components.DropdownPicker
import com.dnfapps.arrmatey.ui.components.ItemDescriptionCard
import com.dnfapps.arrmatey.ui.components.LabelledSwitch
import com.dnfapps.arrmatey.ui.components.OverlayTopAppBar
import com.dnfapps.arrmatey.ui.components.UpcomingDateView
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPreviewScreen(
    item: ArrMedia,
    type: InstanceType,
    viewModel: MediaPreviewViewModel = koinInjectParams(type),
    navigationManager: NavigationManager = koinInject(),
    navigation: Navigation<ArrScreen> = navigationManager.arr(type)
) {
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val lastAddedItemId by viewModel.lastAddedItemId.collectAsStateWithLifecycle()
    val addItemStatus by viewModel.addItemStatus.collectAsStateWithLifecycle()
    val qualityProfiles by viewModel.qualityProfiles.collectAsStateWithLifecycle()
    val rootFolders by viewModel.rootFolders.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()

    val successMessage = stringResource(R.string.success)

    LaunchedEffect(addItemStatus) {
        when (addItemStatus) {
            is OperationStatus.Success -> {
                showBottomSheet = false
                viewModel.resetAddStatus()
                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    LaunchedEffect(lastAddedItemId) {
        lastAddedItemId?.let { id ->
            showBottomSheet = false
            val newScreen = ArrScreen.Details(id)
            navigation.replaceCurrent(newScreen)
            viewModel.clearLastAddedItemId()
        }
    }

    Scaffold { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues.copy(bottom = 0.dp, top = 0.dp))
            .fillMaxSize()
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
                        onClick = { showBottomSheet = true },
                        colors = IconButtonDefaults.headerBarColors()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null
                        )
                    }
                }
            )

            if (showBottomSheet) {
                AddMediaSheet(
                    item = item,
                    addItemStatus = addItemStatus,
                    qualityProfiles = qualityProfiles,
                    rootFolders = rootFolders,
                    tags = tags,
                    onAddItem = { newItem ->
                        viewModel.addItem(newItem)
                    },
                    onDismiss = { showBottomSheet = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMediaSheet(
    item: ArrMedia,
    addItemStatus: OperationStatus,
    qualityProfiles: List<QualityProfile>,
    rootFolders: List<RootFolder>,
    tags: List<Tag>,
    onAddItem: (ArrMedia) -> Unit,
    onDismiss: () -> Unit
) {
    when (item) {
        is ArrSeries -> AddSeriesForm(
            item,
            addItemStatus,
            qualityProfiles,
            rootFolders,
            tags,
            onAddItem,
            onDismiss
        )
        is ArrMovie -> AddMovieForm(
            item,
            addItemStatus,
            qualityProfiles,
            rootFolders,
            tags,
            onAddItem,
            onDismiss
        )
    }
}

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun AddSeriesForm(
    item: ArrSeries,
    addItemStatus: OperationStatus,
    qualityProfiles: List<QualityProfile>,
    rootFolders: List<RootFolder>,
    tags: List<Tag>,
    onAddItem: (ArrMedia) -> Unit,
    onDismiss: () -> Unit
) {
    LaunchedEffect(addItemStatus) {
        when(addItemStatus) {
            is OperationStatus.Success -> {
                onDismiss()
                // check claude todo
            }
            else -> {}
        }
    }

    var monitor by remember { mutableStateOf(SeriesMonitorType.All) }
    var qualityProfile by remember { mutableStateOf(qualityProfiles.first()) }
    var seriesType by remember { mutableStateOf(SeriesType.Standard) }
    var seasonFolders by remember { mutableStateOf(true) }
    var rootFolder by remember { mutableStateOf(rootFolders.first()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DropdownPicker(
                options = SeriesMonitorType.entries.filter {
                    it != SeriesMonitorType.Unknown &&
                    it != SeriesMonitorType.LatestSeason &&
                    it != SeriesMonitorType.Skip
                },
                modifier = Modifier.fillMaxWidth(),
                selectedOption = monitor,
                onOptionSelected = { monitor = it },
                getOptionLabel = { stringResource(it.stringResource()) },
                label = { Text(stringResource(R.string.monitor)) }
            )

            LabelledSwitch(
                label = stringResource(R.string.season_folders),
                checked = seasonFolders,
                onCheckedChange = { seasonFolders = it }
            )

            DropdownPicker(
                options = qualityProfiles,
                modifier = Modifier.fillMaxWidth(),
                selectedOption = qualityProfile,
                onOptionSelected = { qualityProfile = it },
                getOptionLabel = { it.name ?: "" },
                label = { Text(stringResource(R.string.quality_profile)) }
            )

            DropdownPicker(
                options = SeriesType.entries,
                modifier = Modifier.fillMaxWidth(),
                selectedOption = seriesType,
                onOptionSelected = { seriesType = it },
                getOptionLabel = { stringResource(it.stringResource()) },
                label = { Text(stringResource(R.string.series_type)) }
            )

            if (rootFolders.size > 1) {
                DropdownPicker(
                    options = rootFolders,
                    modifier = Modifier.fillMaxWidth(),
                    selectedOption = rootFolder,
                    onOptionSelected = { rootFolder = it },
                    label = { Text(stringResource(R.string.root_folder)) },
                    getOptionLabel = { "${it.path} (${it.freeSpace.bytesAsFileSizeString()})" }
                )
            }


            Button(
                onClick = {
                    val newItem = item.copyForCreation(
                        monitor = monitor,
                        qualityProfileId = qualityProfile.id,
                        seriesType = seriesType,
                        seasonFolder = seasonFolders,
                        rootFolderPath = rootFolder.path
                    )
                    onAddItem(newItem)
                },
                enabled = addItemStatus !is OperationStatus.InProgress
            ) {
                if (addItemStatus is OperationStatus.InProgress) {
                    CircularProgressIndicator(Modifier.size(24.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.save)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AddMovieForm(
    item: ArrMovie,
    addItemStatus: OperationStatus,
    qualityProfiles: List<QualityProfile>,
    rootFolders: List<RootFolder>,
    tags: List<Tag>,
    onAddItem: (ArrMedia) -> Unit,
    onDismiss: () -> Unit
) {
    LaunchedEffect(addItemStatus) {
        when (addItemStatus) {
            is OperationStatus.Success -> {
                onDismiss()
            }
            else -> {}
        }
    }

    var monitored by remember { mutableStateOf(true) }
    var minimumAvailability by remember { mutableStateOf(MediaStatus.Announced) }
    var qualityProfile by remember { mutableStateOf(qualityProfiles.first()) }
    var rootFolder by remember { mutableStateOf(rootFolders.first()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LabelledSwitch(
                label = stringResource(R.string.monitored),
                checked = monitored,
                onCheckedChange = { monitored = it }
            )

            DropdownPicker(
                options = qualityProfiles,
                modifier = Modifier.fillMaxWidth(),
                selectedOption = qualityProfile,
                onOptionSelected = { qualityProfile = it },
                getOptionLabel = { it.name ?: "" },
                label = { Text(stringResource(R.string.quality_profile)) }
            )

            DropdownPicker(
                options = listOf(
                    MediaStatus.Announced,
                    MediaStatus.InCinemas,
                    MediaStatus.Released
                ),
                modifier = Modifier.fillMaxWidth(),
                selectedOption = minimumAvailability,
                onOptionSelected = { minimumAvailability = it },
                getOptionLabel = { stringResource(it.stringResource()) },
                label = { Text(stringResource(R.string.minimum_availability)) }
            )

            if (rootFolders.size > 1) {
                DropdownPicker(
                    options = rootFolders,
                    modifier = Modifier.fillMaxWidth(),
                    selectedOption = rootFolder,
                    onOptionSelected = { rootFolder = it },
                    label = { Text(stringResource(R.string.root_folder)) },
                    getOptionLabel = { "${it.path} (${it.freeSpace.bytesAsFileSizeString()})" }
                )
            }

            Button(
                onClick = {
                    val newItem = item.copyForCreation(
                        monitored = monitored,
                        minimumAvailability = minimumAvailability,
                        qualityProfileId = qualityProfile.id,
                        rootFolderPath = rootFolder.path
                    )
                    onAddItem(newItem)
                },
                enabled = addItemStatus !is OperationStatus.InProgress
            ) {
                if (addItemStatus is OperationStatus.InProgress) {
                    CircularProgressIndicator(Modifier.size(24.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.save)
                    )
                }
            }
        }
    }
}