package com.dnfapps.arrmatey.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dnfapps.arrmatey.api.sonarr.SonarrClient
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SonarrConfigurationScreen() {
    MaterialTheme {
        val sonarrClient = koinInject<SonarrClient>()

        var apiEndpoint by remember { mutableStateOf("") }
        var apiKey by remember { mutableStateOf("") }

        var testing by remember { mutableStateOf(false) }
        var result: Boolean? by remember { mutableStateOf(null) }

        LaunchedEffect(testing) {
            if (testing) {
                result = sonarrClient.test(apiEndpoint, apiKey)
                testing = false
            }
        }

        Scaffold(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            topBar = { TopAppBar(title = { Text("Settings") }) }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(it),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = apiEndpoint,
                    onValueChange = { apiEndpoint = it },
                    label = { Text("Host") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        testing = true
                    },
                    enabled = !testing
                ) {
                    if (testing) CircularProgressIndicator() else Text("Test")
                }


                result?.let {
                    Text(if (it) "Success" else "Failure")
                }
            }
        }
    }
}