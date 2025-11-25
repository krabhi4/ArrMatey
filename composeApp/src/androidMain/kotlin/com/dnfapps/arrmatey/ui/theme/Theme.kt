package com.dnfapps.arrmatey.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorPalette = darkColorScheme(
    primary = A1_200,
    onPrimary = A1_800,
    primaryContainer = A1_700,
    onPrimaryContainer = A1_100,
    inversePrimary = A1_600,
    secondary = A2_200,
    onSecondary = A2_800,
    secondaryContainer = A2_700,
    onSecondaryContainer = A2_100,
    tertiary = A3_200,
    onTertiary = A3_800,
    tertiaryContainer = A3_700,
    onTertiaryContainer = A3_100,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    outline = N2_400,
    background = N1_900,
    onBackground = N1_100,
    surface = N1_900,
    onSurface = N1_100,
    surfaceVariant = N2_700,
    onSurfaceVariant = N2_200,
    inverseSurface = N1_100,
    inverseOnSurface = N1_900,
)

private val LightColorPalette = lightColorScheme(
    primary = A1_600,
    onPrimary = A1_0,
    primaryContainer = A1_100,
    onPrimaryContainer = A1_900,
    inversePrimary = A1_200,
    secondary = A2_600,
    onSecondary = A2_0,
    secondaryContainer = A2_100,
    onSecondaryContainer = A2_900,
    tertiary = A3_600,
    onTertiary = A3_0,
    tertiaryContainer = A3_100,
    onTertiaryContainer = A3_900,
    error = ErrorLight,
    onError = White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    outline = N2_500,
    background = N1_10,
    onBackground = N1_900,
    surface = N1_10,
    onSurface = N1_900,
    surfaceVariant = N2_100,
    onSurfaceVariant = N2_700,
    inverseSurface = N1_800,
    inverseOnSurface = N1_50
)

@Composable
fun ArrMateyTheme(
    content: @Composable () -> Unit
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val darkTheme = isSystemInDarkTheme()

    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}