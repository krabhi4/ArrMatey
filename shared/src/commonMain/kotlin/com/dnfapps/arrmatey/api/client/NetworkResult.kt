package com.dnfapps.arrmatey.api.client

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T): NetworkResult<T>()
    data class HttpError(
        val code: Int,
        val message: String? = null
    ): NetworkResult<Nothing>()
    data class NetworkError(
        val message: String? = null,
        val cause: Throwable? = null
    ): NetworkResult<Nothing>()
    data class UnexpectedError(
        val cause: Throwable
    ): NetworkResult<Nothing>()
}