package com.dnfapps.arrmatey.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dnfapps.arrmatey.database.dao.ConflictField
import com.dnfapps.arrmatey.database.dao.InsertResult
import com.dnfapps.arrmatey.instances.model.InstanceHeader
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.instances.state.AddInstanceUiState
import com.dnfapps.arrmatey.shared.MR
import com.dnfapps.arrmatey.ui.components.AMOutlinedTextField
import com.dnfapps.arrmatey.utils.mokoString
import com.dnfapps.arrmatey.utils.thenGet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArrConfigurationScreen(
    instanceType: InstanceType,
    uiState: AddInstanceUiState,
    onApiEndpointChanged: (String) -> Unit,
    onApiKeyChanged: (String) -> Unit,
    onInstanceLabelChanged: (String) -> Unit,
    onIsSlowInstanceChanged: (Boolean) -> Unit,
    onCustomTimeoutChanged: (Long?) -> Unit,
    onHeadersChanged: (List<InstanceHeader>) -> Unit,
    onTestConnection: () -> Unit
) {
    val apiEndpoint = uiState.apiEndpoint
    val apiKey = uiState.apiKey
    val instanceLabel = uiState.instanceLabel

    val endpointError = uiState.endpointError
    val isTesting = uiState.testing
    val testResult = uiState.testResult

    val isSlowInstance = uiState.isSlowInstance
    val customTimeout = uiState.customTimeout
    val headers = uiState.headers

    val createResult = uiState.createResult

    val hasLabelConflict = remember(createResult) {
        (createResult as? InsertResult.Conflict)
            ?.fields
            ?.contains(ConflictField.InstanceLabel) == true
    }

    val hasUrlConflict = remember(createResult) {
        (createResult as? InsertResult.Conflict)
            ?.fields
            ?.contains(ConflictField.InstanceUrl) == true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AMOutlinedTextField(
            label = mokoString(MR.strings.label),
            value = instanceLabel,
            onValueChange = onInstanceLabelChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = instanceType.toString(),
            singleLine = true,
            isError = hasLabelConflict,
            errorMessage = hasLabelConflict thenGet mokoString(MR.strings.instance_label_exists)
        )

        AMOutlinedTextField(
            label = mokoString(MR.strings.host),
            required = true,
            value = apiEndpoint,
            onValueChange = onApiEndpointChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = mokoString(MR.strings.host_placeholder) + "${instanceType.defaultPort}",
            description = mokoString(MR.strings.host_description, instanceType.name),
            singleLine = true,
            isError = endpointError || hasUrlConflict,
            errorMessage = when {
                endpointError -> mokoString(MR.strings.invalid_host)
                hasUrlConflict -> mokoString(MR.strings.instance_url_exists)
                else -> null
            }
        )

        AMOutlinedTextField(
            label = mokoString(MR.strings.api_key),
            required = true,
            value = apiKey,
            onValueChange = onApiKeyChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = mokoString(MR.strings.api_key_placeholder),
            singleLine = true
        )

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                FilledTonalButton(
                    onClick = onTestConnection,
                    enabled = !isTesting && apiEndpoint.isNotBlank() && apiKey.isNotBlank()
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    Text(text = mokoString(MR.strings.test))
                }

                testResult?.let { result ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (result) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = mokoString(MR.strings.success),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = mokoString(MR.strings.failure),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = isSlowInstance,
                            onValueChange = onIsSlowInstanceChanged
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mokoString(MR.strings.slow_instance),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isSlowInstance,
                        onCheckedChange = null
                    )
                }

                AMOutlinedTextField(
                    value = customTimeout?.toString() ?: "",
                    onValueChange = { onCustomTimeoutChanged(it.toLongOrNull()) },
                    modifier = Modifier.fillMaxWidth(),
                    label = mokoString(MR.strings.custom_timeout_seconds),
                    enabled = isSlowInstance,
                    placeholder = "300",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = mokoString(MR.strings.custom_headers),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = mokoString(MR.strings.custom_headers_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (headers.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }

                HeadersEditor(
                    headers = headers,
                    onHeadersChanged = onHeadersChanged
                )
            }
        }
    }
}

@Composable
private fun HeadersEditor(
    headers: List<InstanceHeader>,
    onHeadersChanged: (List<InstanceHeader>) -> Unit,
    modifier: Modifier = Modifier
) {
    var headersList by remember { mutableStateOf(headers) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        headersList.forEachIndexed { index, header ->
            HeaderItem(
                header = header,
                onHeaderChanged = { newHeader ->
                    val updated = headersList.toMutableList().apply {
                        set(index, newHeader)
                    }
                    headersList = updated
                    onHeadersChanged(updated)
                },
                onDelete = {
                    val updated = headersList.toMutableList().apply {
                        removeAt(index)
                    }
                    headersList = updated
                    onHeadersChanged(updated)
                }
            )
        }

        OutlinedButton(
            onClick = {
                val updated = headersList + InstanceHeader("", "")
                headersList = updated
                onHeadersChanged(updated)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(mokoString(MR.strings.add_header))
        }
    }
}

@Composable
private fun HeaderItem(
    header: InstanceHeader,
    onHeaderChanged: (InstanceHeader) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AMOutlinedTextField(
            value = header.key,
            onValueChange = { onHeaderChanged(header.copy(key = it)) },
            label = mokoString(MR.strings.header_name),
            modifier = Modifier.weight(1f),
            placeholder = "X-Custom-Header",
            singleLine = true
        )

        AMOutlinedTextField(
            value = header.value,
            onValueChange = { onHeaderChanged(header.copy(value = it)) },
            label = mokoString(MR.strings.header_value),
            modifier = Modifier.weight(1f),
            placeholder = "value",
            singleLine = true
        )

        IconButton(
            onClick = onDelete,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = mokoString(MR.strings.delete),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}