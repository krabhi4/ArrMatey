package com.dnfapps.arrmatey.ui.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dnfapps.arrmatey.navigation.NavigationViewModel
import com.dnfapps.arrmatey.navigation.SettingsScreen
import com.dnfapps.arrmatey.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTab() {
    val viewModel = viewModel<NavigationViewModel>()

    Scaffold {
        Column(modifier = Modifier.padding(it)) {
            Button(
                onClick = {
                    viewModel.navigateToSettingsScreen(SettingsScreen.AddInstance)
                }
            ) {
                Text(text = stringResource(R.string.add_instance))
            }
        }
    }
}