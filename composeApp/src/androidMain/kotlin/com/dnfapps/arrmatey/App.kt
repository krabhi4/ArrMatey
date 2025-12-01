package com.dnfapps.arrmatey

import androidx.compose.runtime.Composable
import com.dnfapps.arrmatey.navigation.AppNavHost
import com.dnfapps.arrmatey.ui.theme.ArrMateyTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    ArrMateyTheme {
        AppNavHost()
    }
}