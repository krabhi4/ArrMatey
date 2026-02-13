package com.dnfapps.arrmatey.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridView
import androidx.compose.ui.graphics.vector.ImageVector
import com.dnfapps.arrmatey.shared.MR
import dev.icerock.moko.resources.StringResource

enum class ViewType(
    val resource: StringResource,
    val imageVector: ImageVector
) {
    Grid(MR.strings.grid_view, Icons.Default.GridView),
    List(MR.strings.list_view, Icons.AutoMirrored.Default.List)
}