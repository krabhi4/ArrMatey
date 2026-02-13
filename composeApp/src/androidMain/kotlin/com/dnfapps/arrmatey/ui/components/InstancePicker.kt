package com.dnfapps.arrmatey.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.dnfapps.arrmatey.compose.icons.Hard_drive
import com.dnfapps.arrmatey.instances.model.Instance

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InstancePicker(
    currentInstance: Instance?,
    typeInstances: List<Instance>,
    onInstanceSelected: (Instance) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val hasMultipleInstances = typeInstances.size > 1

    if (hasMultipleInstances) {
        Box(modifier = modifier) {
            IconButton(
                onClick = { isExpanded = true }
            ) {
                Icon(Hard_drive, null)
            }

            DropdownMenuPopup(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
            ) {
                DropdownMenuGroup(
                    shapes = MenuDefaults.groupShape(0, 1),
                    interactionSource = interactionSource,
                    containerColor = MenuDefaults.groupVibrantContainerColor
                ) {
                    typeInstances.forEachIndexed { index, inst ->
                        DropdownMenuItem(
                            text = { Text(inst.label) },
                            selected = inst.id == currentInstance?.id,
                            shapes = MenuDefaults.itemShape(index, typeInstances.size),
                            colors = MenuDefaults.selectableItemVibrantColors(),
                            onClick = {
                                isExpanded = false
                                onInstanceSelected(inst)
                            },
                            selectedLeadingIcon = { Icon(Icons.Default.Check, null) }
                        )
                    }
                }
            }
        }
    }
}