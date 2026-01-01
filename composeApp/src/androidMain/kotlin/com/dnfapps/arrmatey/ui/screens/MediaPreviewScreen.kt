package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.api.arr.model.MovieStatus
import com.dnfapps.arrmatey.api.arr.model.SeriesAddOptions
import com.dnfapps.arrmatey.api.arr.model.SeriesMonitorType
import com.dnfapps.arrmatey.api.arr.model.SeriesType
import com.dnfapps.arrmatey.api.arr.viewmodel.DetailsUiState
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.entensions.stringResource
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.ArrTabNavigation
import com.dnfapps.arrmatey.ui.components.DropdownPicker
import com.dnfapps.arrmatey.ui.components.OverlayTopAppBar
import com.dnfapps.arrmatey.ui.helpers.statusBarHeight
import com.dnfapps.arrmatey.ui.tabs.LocalArrViewModel
import com.skydoves.cloudy.cloudy
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPreviewScreen(
    item: AnyArrMedia,
    type: InstanceType,
    navigation: ArrTabNavigation = koinInject<ArrTabNavigation>(parameters = { parametersOf(type) })
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

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

                    ItemDescriptionCard(item)
                }
            }

            OverlayTopAppBar(
                scrollState = scrollState,
                modifier = Modifier.align(Alignment.TopCenter),
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
                actions = {
                    IconButton(
                        onClick = { showBottomSheet = true }
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
                    onDismiss = { showBottomSheet = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMediaSheet(
    item: AnyArrMedia,
    onDismiss: () -> Unit
) {
    when (item) {
        is ArrSeries -> AddSeriesForm(item, onDismiss)
        is ArrMovie -> AddMovieForm(item, onDismiss)
    }
}

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun AddSeriesForm(
    item: ArrSeries,
    onDismiss: () -> Unit,
    navigation: ArrTabNavigation = koinInject<ArrTabNavigation> { parametersOf(InstanceType.Sonarr) }
) {
    val arrViewModel = LocalArrViewModel.current
    if (arrViewModel == null) return

    val context = LocalContext.current

    val uiState by arrViewModel.addItemUiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is DetailsUiState.Success<out AnyArrMedia> -> {
                onDismiss()
                state.item.id?.let { id ->
                    val newScreen = ArrScreen.Details(InstanceType.Sonarr, id)
                    navigation.replaceCurrent(newScreen)
                }
            }
            else -> {}
        }
    }

    val qualityProfiles by arrViewModel.qualityProfiles.collectAsStateWithLifecycle()
    val tags by arrViewModel.tags.collectAsStateWithLifecycle()
    val rootFolders by arrViewModel.rootFolders.collectAsStateWithLifecycle()

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
            Button(
                onClick = {
                    val newItem = item.copy(
                        addOptions = SeriesAddOptions(monitor = monitor),
                        qualityProfileId = qualityProfile.id,
                        seriesType = seriesType,
                        seasonFolder = seasonFolders,
                        rootFolderPath = rootFolder.path
                    )
                    arrViewModel.addItem(newItem)
                },
                enabled = uiState !is DetailsUiState.Success<*>
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }

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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = seasonFolders,
                        onValueChange = { seasonFolders = it }
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.season_folders)
                )
                Switch(
                    checked = seasonFolders,
                    onCheckedChange = null
                )
            }

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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AddMovieForm(
    item: ArrMovie,
    onDismiss: () -> Unit,
    navigation: ArrTabNavigation = koinInject<ArrTabNavigation> { parametersOf(InstanceType.Radarr) }
) {
    val arrViewModel = LocalArrViewModel.current
    if (arrViewModel == null) return

    val uiState by arrViewModel.addItemUiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is DetailsUiState.Success<out AnyArrMedia> -> {
                onDismiss()
                state.item.id?.let { id ->
                    val newScreen = ArrScreen.Details(InstanceType.Radarr, id)
                    navigation.replaceCurrent(newScreen)
                }
            }
            else -> {}
        }
    }

    val qualityProfiles by arrViewModel.qualityProfiles.collectAsStateWithLifecycle()
    val tags by arrViewModel.tags.collectAsStateWithLifecycle()
    val rootFolders by arrViewModel.rootFolders.collectAsStateWithLifecycle()

    var monitored by remember { mutableStateOf(true) }
    var minimumAvailability by remember { mutableStateOf(MovieStatus.Announced) }
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
            Button(
                onClick = {
                    val newItem = item.copy(
                        monitored = monitored,
                        minimumAvailability = minimumAvailability,
                        qualityProfileId = qualityProfile.id,
                        rootFolderPath = rootFolder.path
                    )
                    arrViewModel.addItem(newItem)
                },
                enabled = uiState !is DetailsUiState.Success<*>
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = monitored,
                        onValueChange = { monitored = it }
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.monitored),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = monitored,
                    onCheckedChange = null
                )
            }

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
                    MovieStatus.Announced,
                    MovieStatus.InCinemas,
                    MovieStatus.Released
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
        }
    }
}