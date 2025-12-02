package com.dnfapps.arrmatey.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.utils.NetworkConnectivityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

//@Composable
//fun rememberNetworkConnectivityState(): Boolean {
//    val networkConnectivityRepository = koinInject<NetworkConnectivityRepository>()
//    val isConnected by networkConnectivityRepository.isConnected.collectAsState()
//
//    LaunchedEffect(Unit) {
//        networkConnectivityRepository.startObserving()
//    }
//    DisposableEffect(Unit) {
//        onDispose {
//            networkConnectivityRepository.stopObserving()
//        }
//    }
//
//    return isConnected
//}

class NetworkConnectivityViewModel: ViewModel(), KoinComponent {

    private val networkConnectivityRepository: NetworkConnectivityRepository by inject()

    val isConnected = networkConnectivityRepository.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        networkConnectivityRepository.startObserving()
    }

    override fun onCleared() {
        super.onCleared()
        networkConnectivityRepository.stopObserving()
    }

}