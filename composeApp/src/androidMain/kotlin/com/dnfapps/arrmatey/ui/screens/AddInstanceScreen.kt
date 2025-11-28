package com.dnfapps.arrmatey.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.compose.screens.ArrConfigurationScreen
import com.dnfapps.arrmatey.compose.screens.viewmodel.AddInstanceScreenViewModel
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.navigation.NavigationViewModel
import com.dnfapps.arrmatey.ui.components.DropdownPicker
import com.dnfapps.arrmatey.ui.components.InstanceInfoCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInstanceScreen() {
    val scope = rememberCoroutineScope()

    val navigationViewModel = viewModel<NavigationViewModel>()
    val addInstanceViewModel = viewModel<AddInstanceScreenViewModel>()

    var selectedInstanceType by remember { mutableStateOf(InstanceType.Sonarr) }
    val showInfoCard by addInstanceViewModel.showInfoCard.collectAsStateWithLifecycle()

    val isTesting by addInstanceViewModel.testing.collectAsStateWithLifecycle()
    val testResult by addInstanceViewModel.result.collectAsStateWithLifecycle()
    val saveButtonEnabled by remember {
        derivedStateOf {
            !isTesting && testResult == true
        }
    }

    LaunchedEffect(selectedInstanceType) {
        addInstanceViewModel.reset()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.add_instance)) },
                navigationIcon = {
                    IconButton(
                        onClick = { navigationViewModel.settingsTabBackStack.removeLastOrNull() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            scope.launch {
                                addInstanceViewModel.saveInstance(selectedInstanceType)
                                navigationViewModel.settingsTabBackStack.removeLastOrNull()
                            }
                        },
                        enabled = saveButtonEnabled,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(text = stringResource(R.string.save))
                    }
                }
            )
        },
    ) { contentPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DropdownPicker(
                modifier = Modifier.fillMaxWidth(),
                options = InstanceType.entries,
                selectedOption = selectedInstanceType,
                onOptionSelected = { selectedInstanceType = it },
                label = { Text(stringResource(R.string.instance_type)) }
            )

            AnimatedVisibility(
                visible = showInfoCard
            ) {
                InstanceInfoCard(selectedInstanceType)
            }

            when (selectedInstanceType) {
                InstanceType.Sonarr -> ArrConfigurationScreen(InstanceType.Sonarr)// { saveButtonEnabled = it == true }
                InstanceType.Radarr -> ArrConfigurationScreen(InstanceType.Radarr)// { saveButtonEnabled = it == true }
            }
        }
    }
}