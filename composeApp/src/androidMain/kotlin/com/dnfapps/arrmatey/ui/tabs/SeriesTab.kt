package com.dnfapps.arrmatey.ui.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.compose.components.PosterGrid
import com.dnfapps.arrmatey.compose.components.SortMenuButton
import com.dnfapps.arrmatey.compose.utils.SortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.compose.utils.applySeriesSorting
import com.dnfapps.arrmatey.ui.viewmodel.InstanceViewModel
import com.dnfapps.arrmatey.ui.viewmodel.rememberSonarrViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SeriesTab() {
    val context = LocalContext.current

    val instanceViewModel = viewModel<InstanceViewModel>()
    val instance by instanceViewModel.getFirstInstance(InstanceType.Sonarr).collectAsState(null)

    var selectedSortOption by remember { mutableStateOf(SortBy.Title) }
    var selectedSortOrder by remember { mutableStateOf(SortOrder.Asc) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.series)) },
                actions = {
                    instance?.let {
                        SortMenuButton(
                            InstanceType.Radarr,
                            onSortChanged = {
                                selectedSortOption = it
                            },
                            onOrderChanged = {
                                selectedSortOrder = it
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
            var loading by remember { mutableStateOf(false) }
            instance?.let { instance ->
                val viewModel = rememberSonarrViewModel(instance)
                val library by viewModel.library.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    loading = true
                    viewModel.refreshLibrary()
                    loading = false
                }

                if (loading) {
                    LoadingIndicator(
                        modifier = Modifier
                            .size(96.dp)
                            .align(Alignment.Center)
                    )
                } else {
                    PosterGrid(
                        items = library.applySeriesSorting(selectedSortOption, selectedSortOrder),
                        onItemClick = {},
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } ?: run {
                Text(text = "No instances found")
            }
        }
    }
}