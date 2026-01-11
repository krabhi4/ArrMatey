package com.dnfapps.arrmatey.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnfapps.arrmatey.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownPicker(
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    getOptionLabel: @Composable (T) -> String = { it.toString() },
    getOptionIcon: (@Composable (T) -> ImageVector)? = null,
    label: @Composable () -> Unit = {},
    includeAllOption: Boolean = false,
    onAllSelected: () -> Unit = {}
) {
    var isDropDownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = isDropDownExpanded,
        onExpandedChange = { isDropDownExpanded = it }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            label()
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                readOnly = true,
                value = when {
                    selectedOption != null -> getOptionLabel(selectedOption)
                    includeAllOption -> "All"
                    else -> "Unknown"
                },
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isDropDownExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
        }
        ExposedDropdownMenu(
            expanded = isDropDownExpanded,
            onDismissRequest = { isDropDownExpanded = false }
        ) {
            if (includeAllOption) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.all)) },
                    onClick = {
                        isDropDownExpanded = false
                        onAllSelected()
                    },
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 6.dp))
            }
            options.forEach { t ->
                DropdownMenuItem(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    text = {
                        getOptionIcon?.let {
                            Icon(
                                imageVector = it(t),
                                contentDescription = null
                            )
                        }
                        Text(
                            text = getOptionLabel(t),
                            fontSize = 16.sp
                        )
                    },
                    onClick = {
                        isDropDownExpanded = false
                        onOptionSelected(t)
                    }
                )
            }
        }
    }
}