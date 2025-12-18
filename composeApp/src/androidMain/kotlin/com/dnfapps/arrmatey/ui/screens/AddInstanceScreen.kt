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
import com.dnfapps.arrmatey.database.dao.InsertResult
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.navigation.SettingsNavigation
import com.dnfapps.arrmatey.ui.components.DropdownPicker
import com.dnfapps.arrmatey.ui.components.InstanceInfoCard
import com.dnfapps.arrmatey.ui.viewmodel.AddInstanceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInstanceScreen() {
    val scope = rememberCoroutineScope()

    val settingsNav = viewModel<SettingsNavigation>()
    val addInstanceViewModel = viewModel<AddInstanceViewModel>()

    var selectedInstanceType by remember { mutableStateOf(InstanceType.Sonarr) }
    val saveButtonEnabled by addInstanceViewModel.saveButtonEnabled.collectAsStateWithLifecycle()
    val infoCardMap by addInstanceViewModel.infoCardMap.collectAsStateWithLifecycle()
    val showInfoCard = infoCardMap[selectedInstanceType] ?: true

    val createResult by addInstanceViewModel.createResult.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        addInstanceViewModel.reset()
    }

    LaunchedEffect(selectedInstanceType) {
        addInstanceViewModel.reset()
        addInstanceViewModel.setInstanceLabel(selectedInstanceType.name)
    }

    LaunchedEffect(createResult) {
        if (createResult is InsertResult.Success) {
            settingsNav.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.add_instance)) },
                navigationIcon = {
                    IconButton(
                        onClick = { settingsNav.popBackStack() }
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
                                addInstanceViewModel.createInstance(selectedInstanceType)
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
            AnimatedVisibility(
                visible = showInfoCard
            ) {
                InstanceInfoCard(selectedInstanceType)
            }

            DropdownPicker(
                modifier = Modifier.fillMaxWidth(),
                options = InstanceType.entries,
                selectedOption = selectedInstanceType,
                onOptionSelected = { selectedInstanceType = it },
                label = { Text(stringResource(R.string.instance_type)) }
            )

            ArrConfigurationScreen(selectedInstanceType)
        }
    }
}