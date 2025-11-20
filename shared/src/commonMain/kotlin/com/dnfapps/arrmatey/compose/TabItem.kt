package com.dnfapps.arrmatey.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector

enum class TabItem(
    val iosIcon: String,
    val androidIcon: ImageVector,
    val textKey: String
) {
    SHOWS("tv", Icons.Default.Tv, "shows"),
    SETTINGS("gear", Icons.Default.Settings, "settings");

    companion object {
        fun allValues() = entries.toList()
    }
}