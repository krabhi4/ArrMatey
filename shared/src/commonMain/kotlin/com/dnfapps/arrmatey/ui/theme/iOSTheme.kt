package com.dnfapps.arrmatey.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// iOS "System" Colors for Light Mode
private val iOSLightColors = lightColorScheme(
    primary = Color(0xFF007AFF),       // iOS Blue
    onPrimary = Color.White,
    surface = Color(0xFFFFFFFF),       // System Background
    onSurface = Color(0xFF000000),      // Label
    background = Color(0xFFFFFFFF),    // System Grouped Background
    onBackground = Color(0xFF000000),
    outline = Color(0xFFC7C7CC)        // System Gray 4
)

// iOS "System" Colors for Dark Mode
private val iOSDarkColors = darkColorScheme(
    primary = Color(0xFF0A84FF),       // iOS Blue (Dark Variant)
    onPrimary = Color.White,
    surface = Color(0xFF1C1C1E),       // Secondary System Background
    onSurface = Color(0xFFFFFFFF),      // Label
    background = Color(0xFF000000),    // System Background
    onBackground = Color(0xFFFFFFFF),
    outline = Color(0xFF38383A)        // System Gray 4 (Dark)
)

@Composable
fun iOSTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()

    val colorScheme = when {
        darkTheme -> iOSDarkColors
        else -> iOSLightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}