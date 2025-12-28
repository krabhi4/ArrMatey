package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.api.arr.viewmodel.LibraryUiState
import com.dnfapps.arrmatey.compose.components.MediaList
import com.dnfapps.arrmatey.compose.components.PosterGrid
import com.dnfapps.arrmatey.compose.utils.SortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.entensions.getString
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.ArrTabNavigation
import com.dnfapps.arrmatey.ui.components.SortMenuButton
import com.dnfapps.arrmatey.ui.tabs.LocalArrViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun ArrSearchScreen(
    initialQuery: String,
    type: InstanceType,
    navigation: ArrTabNavigation = koinInject<ArrTabNavigation>(parameters = { parametersOf(type) })
) {
    val arrViewModel = LocalArrViewModel.current

    var searchQuery by rememberSaveable { mutableStateOf(initialQuery) }

    var sortBy by rememberSaveable { mutableStateOf(SortBy.Relevance) }
    var sortOrder by rememberSaveable { mutableStateOf(SortOrder.Asc) }

    LaunchedEffect(Unit) {
        snapshotFlow { searchQuery }
            .debounce(500)
            .distinctUntilChanged()
            .collect { query ->
                arrViewModel?.performLookup(query)
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
                    SortMenuButton(
                        instanceType = type,
                        sortBy = sortBy,
                        onSortChanged = { sortBy = it },
                        sortOrder = sortOrder,
                        onOrderChanged = { sortOrder = it },
                        limitToLookup = true
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues.copy(bottom = 0.dp))
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
                    placeholder = { Text(stringResource(R.string.search)) },
                    shape = RoundedCornerShape(10.dp)
                )

                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                ) {
                    arrViewModel?.let { arrViewModel ->
                        val lookupState by arrViewModel.lookupUiState.collectAsStateWithLifecycle()

                        when (val state = lookupState) {
                            is LibraryUiState.Initial -> {}
                            is LibraryUiState.Loading -> {
                                LoadingIndicator(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .align(Alignment.Center)
                                )
                            }

                            is LibraryUiState.Success -> {
                                val sortedItems = remember(state.items, sortBy, sortOrder) {
                                    val sorted = when (sortBy) {
                                        SortBy.Relevance -> state.items
                                        SortBy.Year -> state.items.sortedBy { it.year }
                                        SortBy.Rating -> state.items.sortedBy { it.ratingScore() }
                                        else -> state.items
                                    }
                                    if (sortOrder == SortOrder.Desc) sorted.reversed() else sorted
                                }

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    PosterGrid(
                                        items = sortedItems,
                                        onItemClick = { item ->
                                            val destination = if (item.id == null) {
                                                ArrScreen.Preview(item, type)
                                            } else {
                                                ArrScreen.Details(type, item.id!!)
                                            }
                                            navigation.navigateTo(destination)
                                        }
                                    )
                                }
                            }

                            is LibraryUiState.Error -> {
                                Text("An error occurred")
                            }
                        }
                    }
                }
            }
        }
    }
}