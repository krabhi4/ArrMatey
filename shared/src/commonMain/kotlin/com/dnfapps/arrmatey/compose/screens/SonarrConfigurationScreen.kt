package com.dnfapps.arrmatey.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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

        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = apiEndpoint,
                onValueChange = { apiEndpoint = it },
                label = { Text("Host") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
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