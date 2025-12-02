package com.dnfapps.arrmatey.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface NetworkConnectivityObserver {
    val isConnected: StateFlow<Boolean>
    fun startObserving()
    fun stopObserving()
}

enum class NetworkStatus {
    Available,
    Unavailable,
    Losing,
    Lost
}

expect class NetworkConnectivityObserverFactory() {
    fun create(): NetworkConnectivityObserver
}

class NetworkConnectivityViewModel: ViewModel(), KoinComponent {
    private val networkObserver: NetworkConnectivityObserver by inject()

    val isConnected = networkObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        networkObserver.startObserving()
    }

    override fun onCleared() {
        super.onCleared()
        networkObserver.stopObserving()
    }
}