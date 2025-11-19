package com.dnfapps.arrmatey

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dnfapps.arrmatey.compose.screens.SonarrConfigurationScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(mainViewModel: MainViewModel = viewModel()) {
    MaterialTheme {
        SonarrConfigurationScreen()
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