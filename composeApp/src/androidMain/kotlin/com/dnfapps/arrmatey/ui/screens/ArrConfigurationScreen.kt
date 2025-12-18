package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.compose.components.AMOutlinedTextField
import com.dnfapps.arrmatey.database.dao.ConflictField
import com.dnfapps.arrmatey.database.dao.InsertResult
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.ui.viewmodel.AddInstanceViewModel
import com.dnfapps.arrmatey.utils.thenGet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArrConfigurationScreen(
    instanceType: InstanceType,
    viewModel: AddInstanceViewModel = viewModel<AddInstanceViewModel>()
) {

    val apiEndpoint by viewModel.apiEndpoint.collectAsStateWithLifecycle()
    val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()
    val instanceLabel by viewModel.instanceLabel.collectAsStateWithLifecycle()

    val endpointError by viewModel.endpointError.collectAsStateWithLifecycle()
    val isTesting by viewModel.testing.collectAsStateWithLifecycle()
    val testResult by viewModel.result.collectAsStateWithLifecycle()

    val isSlowInstance by viewModel.isSlowInstance.collectAsStateWithLifecycle()
    val customTimeout by viewModel.customTimeout.collectAsStateWithLifecycle()

    val createResult by viewModel.createResult.collectAsStateWithLifecycle()

    val hasLabelConflict by remember { derivedStateOf {
        (createResult as? InsertResult.Conflict)?.fields?.contains(ConflictField.InstanceUrl) == true
    } }
    val hasUrlConflict by remember { derivedStateOf {
        (createResult as? InsertResult.Conflict)?.fields?.contains(ConflictField.InstanceUrl) == true
    } }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AMOutlinedTextField(
            label = stringResource(R.string.label),
            value = instanceLabel,
            onValueChange = { viewModel.setInstanceLabel(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = instanceType.toString(),
            singleLine = true,
            isError = hasLabelConflict,
            errorMessage = hasLabelConflict thenGet stringResource(R.string.instance_label_exists)
        )

        AMOutlinedTextField(
            label = stringResource(R.string.host),
            required = true,
            value = apiEndpoint,
            onValueChange = {
                viewModel.setApiEndpoint(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.host_placeholder) + "${instanceType.defaultPort}",
            description = stringResource(R.string.host_description, instanceType.toString()),
            singleLine = true,
            isError = endpointError || hasUrlConflict,
            errorMessage = when {
                endpointError -> stringResource(R.string.invalid_host)
                hasUrlConflict -> stringResource(R.string.instance_url_exists)
                else -> null
            }
        )

        AMOutlinedTextField(
            label = stringResource(R.string.api_key),
            required = true,
            value = apiKey,
            onValueChange = {
                viewModel.setApiKey(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.api_key_placeholder),
            singleLine = true
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
            //spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    viewModel.testConnection()
                },
                enabled = !isTesting && apiEndpoint.isNotBlank() && apiKey.isNotBlank()
            ) {
                if (isTesting) CircularProgressIndicator() else Text(text = stringResource(R.string.test))
            }

            testResult?.let { result ->
                if (result) {
                    Text(text = "✅ ${stringResource(R.string.success)}", color = Color.Green)
                } else {
                    Text(text = "❌ ${stringResource(R.string.failure)}", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = isSlowInstance,
                    onValueChange = { viewModel.setIsSlowInstance(it) }
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.slow_instance)
            )
            Switch(
                checked = isSlowInstance,
                onCheckedChange = null
            )
        }
        AMOutlinedTextField(
            value = customTimeout?.toString() ?: "",
            onValueChange = {
                viewModel.setCustomTimeout(it.toLongOrNull())
            },
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.custom_timeout_seconds),
            enabled = isSlowInstance,
            placeholder = "300",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}