package com.dnfapps.arrmatey.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlayTopAppBar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    val headerBackgroundAlpha by remember {
        derivedStateOf {
            // Fade in over 200 pixels of scroll
            val fadeDistance = 200f
            (scrollState.value / fadeDistance).coerceIn(0f, 1f)
        }
    }

    TopAppBar(
        title = {
            AnimatedVisibility(
                visible = headerBackgroundAlpha > 0.9f,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                title()
            }
        },
        navigationIcon = navigationIcon,
        actions = actions,
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(
                alpha = headerBackgroundAlpha
            )
        )
    )
}