package com.dnfapps.arrmatey.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
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
import com.dnfapps.arrmatey.api.arr.model.IArrRelease
import com.dnfapps.arrmatey.api.arr.model.ReleaseParams
import com.dnfapps.arrmatey.api.arr.viewmodel.DownloadState
import com.dnfapps.arrmatey.api.arr.viewmodel.LibraryUiState
import com.dnfapps.arrmatey.compose.components.ProgressBox
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.extensions.formatAgeMinutes
import com.dnfapps.arrmatey.navigation.ArrTabNavigation
import com.dnfapps.arrmatey.ui.tabs.LocalArrTabNavigation
import com.dnfapps.arrmatey.ui.tabs.LocalArrViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InteractiveSearchScreen(
    releaseParams: ReleaseParams,
    navigation: ArrTabNavigation = LocalArrTabNavigation.current
) {
    val arrViewModel = LocalArrViewModel.current
    if (arrViewModel == null) return

    val context = LocalContext.current

    val releaseUiState by arrViewModel.releaseUiState.collectAsStateWithLifecycle()
    val downloadState by arrViewModel.downloadReleaseState.collectAsStateWithLifecycle(DownloadState.Initial)

    var confirmRelease by remember { mutableStateOf<IArrRelease?>( null) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val downloadQueueSuccessMessage = stringResource(R.string.download_queue_success)
    val downloadQueueErrorMessage = stringResource(R.string.download_queue_error)

    LaunchedEffect(downloadState) {
        when(downloadState) {
            is DownloadState.Success -> {
                Toast.makeText(context, downloadQueueSuccessMessage, Toast.LENGTH_SHORT).show()
            }
            is DownloadState.Error -> {
                Toast.makeText(context, downloadQueueErrorMessage, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    LaunchedEffect(releaseParams) {
        arrViewModel.getReleases(releaseParams)
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
                            if (!showSearch) searchQuery = ""
                        }
                    ) {
                        Crossfade(showSearch) { isShown ->
                            if (isShown) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.cancel)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.search)
                                )
                            }
                        }
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
                    val filteredItems = remember(searchQuery) {
                        if (searchQuery.isEmpty()) state.items
                        else state.items.filter {
                            it.title.contains(searchQuery, ignoreCase = true)
                        }
                    }

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
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier
                                        .padding(vertical = 12.dp)
                                        .fillMaxWidth(),
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = null,
                                            modifier = Modifier.clickable { searchQuery = "" }
                                        )
                                    },
                                    placeholder = { Text(stringResource(R.string.search)) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                        items(filteredItems) { item ->
                            val shouldAnimate = (downloadState as? DownloadState.Loading)?.guid == item.guid
                            ReleaseItem(
                                item = item,
                                onItemClick = {
                                    if (item.downloadAllowed) {
                                        arrViewModel.downloadRelease(item)
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
                    Text(state.error.message)
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
                                arrViewModel.downloadRelease(release, force = true)
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
    }
}

@Composable
fun <T: IArrRelease> ReleaseItem(
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
                    append(" • ")
                    append(item.quality.qualityLabel)
                    append(" • ")
                    append(item.size.bytesAsFileSizeString())
                }
                Text(
                    text = secondLine,
                    maxLines = 1
                )

                val thirdLine = listOf(
                    item.languageLabel,
                    item.indexerLabel,
                    item.ageMinutes.formatAgeMinutes()
                ).joinToString(" • ")
                Text(
                    text = thirdLine,
                    maxLines = 1
                )
            }
        }
    }
}