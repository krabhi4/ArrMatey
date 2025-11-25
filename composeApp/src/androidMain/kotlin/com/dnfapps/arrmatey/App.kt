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
//        SonarrConfigurationScreen()
//        var showContent by remember { mutableStateOf(false) }
//        val greetings by mainViewModel.greetingsList.collectAsStateWithLifecycle()
//
//        Column(
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.primaryContainer)
//                .safeContentPadding()
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                Column(
//                    modifier = Modifier
//                        .background(MaterialTheme.colorScheme.primaryContainer)
//                        .safeContentPadding()
//                        .fillMaxSize(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    greetings.forEach { greeting ->
//                        Text(greeting)
//                        HorizontalDivider()
//                    }
//                }
//            }
//        }
    }
}