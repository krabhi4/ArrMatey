package com.dnfapps.arrmatey.ui.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.ui.components.PosterGrid
import com.dnfapps.arrmatey.ui.viewmodel.InstanceViewModel
import com.dnfapps.arrmatey.ui.viewmodel.rememberSonarrViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SeriesTab() {
    val instanceViewModel = viewModel<InstanceViewModel>()

    val instance by instanceViewModel.getFirstInstance(InstanceType.Sonarr).collectAsState(null)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.series)) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
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
                        items = library,
                        onItemClick = {},
                        modifier = Modifier.fillMaxSize()
                    )
                }
//                Column {
//                    library.forEach {
//                        Text(text = it.title ?: "")
//                    }
//                }
            } ?: run {
                Text(text = "No instances found")
            }
        }
    }
}