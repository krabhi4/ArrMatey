package com.dnfapps.arrmatey.entensions

import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun IconButtonDefaults.headerBarColors() = iconButtonColors(
    containerColor = MaterialTheme.colorScheme.background.copy(alpha = .8f)
)