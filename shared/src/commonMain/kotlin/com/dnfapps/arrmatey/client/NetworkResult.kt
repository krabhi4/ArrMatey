package com.dnfapps.arrmatey.client

import com.dnfapps.arrmatey.arr.api.client.HasArrImages
import com.dnfapps.arrmatey.client.NetworkResult.Error
import com.dnfapps.arrmatey.client.NetworkResult.Loading
import com.dnfapps.arrmatey.client.NetworkResult.Success

sealed interface NetworkResult<out T> {
    object Loading : NetworkResult<Nothing>
    data class Success<T>(val data: T): NetworkResult<T>
    data class Error(
        val code: Int? = null,
        val message: String? = null,
        val cause: Throwable? = null
    ): NetworkResult<Nothing>

    fun <R> map(transform: (T) -> R): NetworkResult<R> {
        return when (this) {
            is Loading -> Loading
            is Error -> Error(code, message, cause)
            is Success -> Success(transform(data))
        }
    }
}

fun <T, R> NetworkResult<List<T>>.mapValues(transform: (T) -> R): NetworkResult<List<R>> {
    return when (this) {
        is Loading -> Loading
        is Error -> Error(code, message, cause)
        is Success -> Success(data = data.map(transform))
    }
}

suspend fun <T> NetworkResult<T>.onSuccess(action: suspend (T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) action(data)
    return this
}

suspend fun <T> NetworkResult<T>.onError(action: suspend (Int?, String?, Throwable?) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) action(code, message, cause)
    return this
}