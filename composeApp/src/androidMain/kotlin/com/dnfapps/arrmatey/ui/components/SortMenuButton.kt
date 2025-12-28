package com.dnfapps.arrmatey.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.compose.utils.SortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.entensions.getString
import com.dnfapps.arrmatey.model.InstanceType

@Composable
fun SortMenuButton(
    instanceType: InstanceType,
    sortBy: SortBy,
    onSortChanged: (SortBy) -> Unit,
    sortOrder: SortOrder,
    onOrderChanged: (SortOrder) -> Unit,
    limitToLookup: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val options = remember {
        if (limitToLookup) {
            SortBy.lookupEntries() }
        else {
            SortBy.typeEntries(instanceType)
        }
    }

    IconButton(
        onClick = { expanded = !expanded }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.Sort,
            contentDescription = stringResource(R.string.sort)
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                onClick = {
                    onSortChanged(option)
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = option.androidIcon,
                            contentDescription = null,
                            tint = if (sortBy == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = getString(option.textKey),
                            color = if (sortBy == option) MaterialTheme.colorScheme.primary else Color.Unspecified
                        )
                    }
                }
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        SortOrder.entries.forEach { order ->
            DropdownMenuItem(
                onClick = {
                    onOrderChanged(order)
                },
                leadingIcon = {
                    if (sortOrder == order) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = order.androidIcon,
                            contentDescription = null,
                            tint = if (sortOrder == order) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = getString(order.iosText),
                            color = if (sortOrder == order) MaterialTheme.colorScheme.primary else Color.Unspecified
                        )
                    }
                }
            )
        }
    }
}