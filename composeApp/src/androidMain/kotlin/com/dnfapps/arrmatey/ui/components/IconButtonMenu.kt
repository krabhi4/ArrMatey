package com.dnfapps.arrmatey.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import com.dnfapps.arrmatey.compose.utils.QueueSortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder

@Composable
fun QueueSortMenu(
    sortBy: QueueSortBy,
    sortOrder: SortOrder,
    onSortByChanged: (QueueSortBy) -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(
        onClick = { expanded = !expanded }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.Sort,
            contentDescription = null
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            QueueSortBy.entries.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onSortByChanged(option)
                    },
                    leadingIcon = {
                        if (sortBy == option) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    text = {
                        Text(
                            text = option.name,
                            color = if (sortBy == option) MaterialTheme.colorScheme.primary
                            else Color.Unspecified
                        )
                    }
                )
            }
            HorizontalDivider()
            SortOrder.entries.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onSortOrderChanged(option)
                    },
                    leadingIcon = {
                        if (sortOrder == option) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    text = {
                        Text(
                            text = option.name,
                            color = if (sortOrder == option) MaterialTheme.colorScheme.primary
                            else Color.Unspecified
                        )
                    }
                )
            }
        }
    }
}