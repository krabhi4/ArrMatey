package com.dnfapps.arrmatey.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.dnfapps.arrmatey.compose.utils.FilterBy
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.utils.mokoString

@Composable
fun FilterMenuButton(
    instanceType: InstanceType,
    selectedFilter: FilterBy,
    onFilterChange: (FilterBy) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val options = remember { FilterBy.typeEntries(instanceType) }

    IconButton(
        onClick = { expanded = !expanded }
    ) {
        Icon(
            imageVector = Icons.Outlined.FilterAlt,
            contentDescription = null
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onFilterChange(option)
                        expanded = false
                    },
                    leadingIcon = {
                        if (selectedFilter == option) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    text = {
                        Text(
                            text = mokoString(option.resource),
                            color = if (selectedFilter == option) MaterialTheme.colorScheme.primary else Color.Unspecified
                        )
                    }
                )
            }
        }
    }
}