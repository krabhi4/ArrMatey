package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.navigation.ArrTabNavigation
import com.dnfapps.arrmatey.ui.components.ExtraFileCard
import com.dnfapps.arrmatey.ui.components.HistoryItemView
import com.dnfapps.arrmatey.ui.components.MovieFileCard
import com.dnfapps.arrmatey.ui.tabs.LocalArrTabNavigation
import com.dnfapps.arrmatey.ui.tabs.LocalArrViewModel
import com.dnfapps.arrmatey.ui.viewmodel.RadarrViewModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun MovieFilesScreen(
    movie: ArrMovie,
    navigation: ArrTabNavigation = LocalArrTabNavigation.current
) {
    val arrViewModel = LocalArrViewModel.current
    if (arrViewModel == null || arrViewModel !is RadarrViewModel) {
        navigation.popBackStack()
        return
    }

    val movieExtraFileMap by arrViewModel.movieExtraFilesMap.collectAsStateWithLifecycle()
    val movieExtraFiles = remember(movieExtraFileMap) {
        movieExtraFileMap[movie.id] ?: emptyList()
    }

    val itemHistoryRefreshing by arrViewModel.itemHistoryRefreshing.collectAsStateWithLifecycle()
    val itemHistoryMap by arrViewModel.itemHistoryMap.collectAsStateWithLifecycle()
    val movieHistoryItems by remember { derivedStateOf {
        itemHistoryMap[movie.id] ?: emptyList()
    } }

    LaunchedEffect(movie.id) {
        movie.id?.let { id ->
            arrViewModel.getItemHistory(id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { navigation.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = itemHistoryRefreshing,
            onRefresh = {
                movie.id?.let { id ->
                    arrViewModel.getItemHistory(id)
                }
            },
            modifier = Modifier
                .padding(paddingValues.copy(bottom = 0.dp))
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.files),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                item {
                    movie.movieFile?.let { file ->
                        MovieFileCard(file)
                    }
                }
                items(movieExtraFiles) { extraFile ->
                    ExtraFileCard(extraFile)
                }
                item {
                    Text(
                        text = stringResource(R.string.history),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                items(movieHistoryItems) { historyItem ->
                    HistoryItemView(historyItem)
                }
                if (movieHistoryItems.isEmpty()) {
                    item {
                        Text(stringResource(R.string.no_history))
                    }
                }
            }
        }
    }
}