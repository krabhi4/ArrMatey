package com.dnfapps.arrmatey.api.client

import io.ktor.network.sockets.SocketTimeoutException
import io.ktor.util.network.UnresolvedAddressException
import okio.IOException
import java.net.ConnectException
import java.net.UnknownHostException

actual fun Throwable.isNoConnectionError(): Boolean = when (this) {
    is UnknownHostException,
    is SocketTimeoutException,
    is ConnectException,
    is UnresolvedAddressException,
    is IOException -> true
    else -> false
}