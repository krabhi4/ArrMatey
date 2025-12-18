package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.dnfapps.arrmatey.navigation.SettingsNavigation
import com.dnfapps.arrmatey.ui.viewmodel.AddInstanceViewModel
import com.dnfapps.arrmatey.ui.viewmodel.InstanceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditInstanceScreen(id: Long) {
    val scope = rememberCoroutineScope()

    val settingsNav = viewModel<SettingsNavigation>()
    val editInstanceViewModel = viewModel<AddInstanceViewModel>()
    val instanceViewModel = viewModel<InstanceViewModel>()

    val instances by instanceViewModel.allInstances.collectAsStateWithLifecycle()
    val instance = remember { instances.firstOrNull { it.id == id } }

    val editResult by editInstanceViewModel.editResult.collectAsStateWithLifecycle()
    val testResult by editInstanceViewModel.result.collectAsStateWithLifecycle()

    var confirmDelete by remember { mutableStateOf(false) }
    var saveClicked by remember { mutableStateOf(false) }

    LaunchedEffect(instance) {
        editInstanceViewModel.reset()
        instance?.let {
            editInstanceViewModel.initialize(it)
        }
    }

    LaunchedEffect(testResult) {
        if (testResult == true && saveClicked) {
            instance?.let {
                editInstanceViewModel.updateInstance(instance)
            }
        }
    }

    LaunchedEffect(editResult) {
        if (editResult is InsertResult.Success) {
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
                    IconButton(
                        onClick = {
                            confirmDelete = true
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                instance?.let {
                                    saveClicked = true
                                    editInstanceViewModel.testConnection()
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(text = stringResource(R.string.save))
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            instance?.let { instance ->
                ArrConfigurationScreen(instance.type)

                if (confirmDelete) {
                    AlertDialog(
                        onDismissRequest = { confirmDelete = false},
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        instanceViewModel.delete(instance)
                                        settingsNav.popBackStack()
                                    }
                                }
                            ) { Text(stringResource(R.string.yes)) }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    confirmDelete = false

                                }
                            ) { Text(stringResource(R.string.no)) }
                        },
                        title = { Text(stringResource(R.string.confirm)) },
                        text = {
                            Text(stringResource(R.string.confirm_delete_instance, instance.label))
                        }
                    )
                }
            }
        }
    }
}