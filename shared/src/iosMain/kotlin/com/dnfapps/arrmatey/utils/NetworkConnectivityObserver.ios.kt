package com.dnfapps.arrmatey.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Network.*
import platform.darwin.dispatch_queue_create
import platform.darwin.DISPATCH_QUEUE_SERIAL

actual class NetworkConnectivityObserverFactory {
    actual fun create(): NetworkConnectivityObserver {
        return IosNetworkConnectivityObserver()
    }
}

class IosNetworkConnectivityObserver : NetworkConnectivityObserver {

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val monitor = nw_path_monitor_create()
    private val queue = dispatch_queue_create("NetworkMonitor", DISPATCH_QUEUE_SERIAL)

    override fun startObserving() {
        nw_path_monitor_set_update_handler(monitor) { path ->
            val status = nw_path_get_status(path)
            _isConnected.value = when (status) {
                nw_path_status_satisfied, nw_path_status_satisfiable -> true
                else -> false
            }
        }

        nw_path_monitor_set_queue(monitor, queue)
        nw_path_monitor_start(monitor)
    }

    override fun stopObserving() {
        nw_path_monitor_cancel(monitor)
    }
}