package com.dnfapps.arrmatey.api.arr.viewmodel

import com.dnfapps.arrmatey.api.arr.IArrClient
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.ArrMedia
import com.dnfapps.arrmatey.api.client.NetworkResult
import com.dnfapps.arrmatey.database.dao.BaseArrDao
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.utils.getCurrentSystemTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

fun createInstanceRepository(instance: Instance): BaseArrRepository<out AnyArrMedia> {
    return when (instance.type) {
        InstanceType.Sonarr -> SonarrRepository(instance)
        InstanceType.Radarr -> RadarrRepository(instance)
    }
}

abstract class BaseArrRepository<T: AnyArrMedia>(
    protected val instance: Instance
): KoinComponent, IArrRepository<T> {

    abstract val client: IArrClient<T>
    abstract val dao: BaseArrDao<T>

    protected val _uiState = MutableStateFlow<LibraryUiState<T>>(LibraryUiState.Initial)
    override val uiState: StateFlow<LibraryUiState<T>> = _uiState.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (instance.cacheOnDisk) {
                _uiState.value = LibraryUiState.Loading
                val cached = dao.getAll(instance.id)
                if (cached.isNotEmpty()) {
                    _uiState.value = LibraryUiState.Success(cached)
                }
            }
            refreshLibrary()
        }
    }

    override suspend fun refreshLibrary() {
        // get current items for optimistic updates
        val currentItems = when (val state = _uiState.value) {
            is LibraryUiState.Success -> state.items
            is LibraryUiState.Error -> state.cachedItems
            else -> emptyList()
        }

        _uiState.value = if (currentItems.isNotEmpty()) {
            LibraryUiState.Success(currentItems, isRefreshing = true)
        } else {
            LibraryUiState.Loading
        }

        val result = client.getLibrary()
        when (result) {
            is NetworkResult.Success -> {
                val newLibrary = result.data
                _uiState.value = LibraryUiState.Success(newLibrary, isRefreshing = false)

                if (instance.cacheOnDisk)
                    withContext(Dispatchers.IO) {
                        newLibrary.map { item ->
                            item.apply { instanceId = instance.id }
                        }.let { items ->
                            dao.clearAll()
                            dao.insertAll(items)
                        }
                    }
            }

            is NetworkResult.NetworkError -> {
                _uiState.value = LibraryUiState.Error(
                    error = ErrorEvent(result.message ?: "Network error occurred"),
                    cachedItems = currentItems,
                    type = LibraryUiError.Network
                )
            }

            is NetworkResult.HttpError -> {
                _uiState.value = LibraryUiState.Error(
                    error = ErrorEvent(result.message ?: "Server error occurred"),
                    cachedItems = currentItems,
                    type = LibraryUiError.Http
                )
            }

            is NetworkResult.UnexpectedError -> {
                _uiState.value = LibraryUiState.Error(
                    error = ErrorEvent(result.cause.message ?: "An unexpected error occurred"),
                    cachedItems = currentItems,
                    type = LibraryUiError.Unexpected
                )
            }
        }
    }

}

sealed interface LibraryUiState<out T> {
    data object Initial: LibraryUiState<Nothing>
    data object Loading: LibraryUiState<Nothing>
    data class Success<T>(
        val items: List<T>,
        val isRefreshing: Boolean = false
    ): LibraryUiState<T>
    data class Error<T>(
        val error: ErrorEvent,
        val type: LibraryUiError,
        val cachedItems: List<T> = emptyList()
    ): LibraryUiState<T>
}

data class ErrorEvent(
    val message: String,
    val timestamp: Long = getCurrentSystemTimeMillis()
)

enum class LibraryUiError {
    Network,
    Http,
    Unexpected
}