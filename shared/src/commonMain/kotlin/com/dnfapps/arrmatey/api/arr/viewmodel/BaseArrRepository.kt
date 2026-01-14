package com.dnfapps.arrmatey.api.arr.viewmodel

import com.dnfapps.arrmatey.api.arr.IArrClient
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.api.arr.model.CommandPayload
import com.dnfapps.arrmatey.api.arr.model.HistoryItem
import com.dnfapps.arrmatey.api.arr.model.IArrRelease
import com.dnfapps.arrmatey.api.arr.model.QualityProfile
import com.dnfapps.arrmatey.api.arr.model.QueuePage
import com.dnfapps.arrmatey.api.arr.model.ReleaseParams
import com.dnfapps.arrmatey.api.arr.model.RootFolder
import com.dnfapps.arrmatey.api.arr.model.Tag
import com.dnfapps.arrmatey.api.client.NetworkResult
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
import kotlinx.io.files.Path
import org.koin.core.component.KoinComponent

fun createInstanceRepository(instance: Instance): BaseArrRepository<out AnyArrMedia, out IArrRelease, out ReleaseParams> {
    return when (instance.type) {
        InstanceType.Sonarr -> SonarrRepository(instance)
        InstanceType.Radarr -> RadarrRepository(instance)
    }
}

abstract class BaseArrRepository<T: AnyArrMedia, R: IArrRelease, P: ReleaseParams>(
    protected val instance: Instance
): KoinComponent, IArrRepository<T, R, P> {

    abstract val client: IArrClient<T, R, P>

    protected val _uiState = MutableStateFlow<LibraryUiState<T>>(LibraryUiState.Initial)
    override val uiState: StateFlow<LibraryUiState<T>> = _uiState.asStateFlow()

    protected val _detailUiState = MutableStateFlow<DetailsUiState<T>>(DetailsUiState.Initial)
    override val detailUiState = _detailUiState

    protected val _lookupUiState = MutableStateFlow<LibraryUiState<T>>(LibraryUiState.Initial)
    override val lookupUiState: StateFlow<LibraryUiState<T>> = _lookupUiState.asStateFlow()

    protected val _qualityProfiles = MutableStateFlow<List<QualityProfile>>(emptyList())
    override val qualityProfiles: StateFlow<List<QualityProfile>> = _qualityProfiles

    protected val _rootFolders = MutableStateFlow<List<RootFolder>>(emptyList())
    override val rootFolders: StateFlow<List<RootFolder>> = _rootFolders

    protected val _tags = MutableStateFlow<List<Tag>>(emptyList())
    override val tags: StateFlow<List<Tag>> = _tags

    protected val _addItemUiState = MutableStateFlow<DetailsUiState<T>>(DetailsUiState.Initial)
    override val addItemUiState: StateFlow<DetailsUiState<T>> = _addItemUiState

    protected val _automaticSearchIds = MutableStateFlow<List<Long>>(emptyList())
    override val automaticSearchIds: StateFlow<List<Long>> = _automaticSearchIds

    protected val _automaticSearchResult = MutableStateFlow<Boolean?>(null)
    override val automaticSearchResult: StateFlow<Boolean?> = _automaticSearchResult

    protected val _itemHistoryMap = MutableStateFlow<Map<Long, List<HistoryItem>>>(emptyMap())
    override val itemHistoryMap: StateFlow<Map<Long, List<HistoryItem>>> = _itemHistoryMap

    protected val _itemHistoryRefreshing = MutableStateFlow(false)
    override val itemHistoryRefreshing: StateFlow<Boolean> = _itemHistoryRefreshing

    protected val _releasesUiState = MutableStateFlow<LibraryUiState<R>>(LibraryUiState.Initial)
    override val releasesUiState: StateFlow<LibraryUiState<R>> = _releasesUiState

    protected val _downloadReleaseState = MutableStateFlow<DownloadState>(DownloadState.Initial)
    override val downloadReleaseState: StateFlow<DownloadState> = _downloadReleaseState

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshLibrary()
            refreshInstance()
        }
    }

    private suspend fun refreshInstance() {
        val qualityProfilesResp = client.getQualityProfiles()
        if (qualityProfilesResp is NetworkResult.Success) {
            _qualityProfiles.emit(qualityProfilesResp.data)
        }

        val rootFoldersResp = client.getRootFolders()
        if (rootFoldersResp is NetworkResult.Success) {
            _rootFolders.emit(rootFoldersResp.data)
        }

        val tagsResp = client.getTags()
        if (tagsResp is NetworkResult.Success) {
            _tags.emit(tagsResp.data)
        }
    }

    override suspend fun refreshLibrary() {
        // get current items for optimistic updates
        val currentItems = when (val state = _uiState.value) {
            is LibraryUiState.Success -> state.items
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
            }

            is NetworkResult.NetworkError -> {
                _uiState.value = LibraryUiState.Error(
                    error = ErrorEvent(result.message ?: "Network error occurred"),
                    type = UiErrorType.Network
                )
            }

            is NetworkResult.HttpError -> {
                _uiState.value = LibraryUiState.Error(
                    error = ErrorEvent(result.message ?: "Server error occurred"),
                    type = UiErrorType.Http
                )
            }

            is NetworkResult.UnexpectedError -> {
                _uiState.value = LibraryUiState.Error(
                    error = ErrorEvent(result.cause.message ?: "An unexpected error occurred"),
                    type = UiErrorType.Unexpected
                )
            }
        }
    }

    override suspend fun getDetails(id: Long) {
        _detailUiState.value = DetailsUiState.Loading

        val result = client.getDetail(id)
        when (result) {
            is NetworkResult.Success -> {
                _detailUiState.value = DetailsUiState.Success(item = result.data)
            }
            is NetworkResult.HttpError -> {
                _detailUiState.value = DetailsUiState.Error(
                    error = ErrorEvent(result.message ?: "Server error occurred"),
                    type = UiErrorType.Http
                )
            }
            is NetworkResult.NetworkError -> {
                _detailUiState.value = DetailsUiState.Error(
                    error = ErrorEvent(result.message ?: "Network error occurred"),
                    type = UiErrorType.Network
                )
            }
            is NetworkResult.UnexpectedError -> {
                _detailUiState.value = DetailsUiState.Error(
                    error = ErrorEvent(result.cause.message ?: "An unexpected error occurred"),
                    type = UiErrorType.Unexpected
                )
            }
        }
    }

    override suspend fun setMonitorStatus(id: Long, monitorStatus: Boolean) {
        val resp = client.setMonitorStatus(id, monitorStatus)

        val item = (_detailUiState.value as? DetailsUiState.Success<T>)?.item
        if (
            resp is NetworkResult.Success
            && resp.data.firstOrNull() != null
            && item != null
        ) {
            val first = resp.data.first()
            if (item is ArrSeries || item is ArrMovie) {
                val newItem = item.setMonitored(
                    monitored = first.monitored
                ) as T
                _detailUiState.value = DetailsUiState.Success(item = newItem)
            }
        }
    }

    override suspend fun lookup(query: String) {
        _lookupUiState.value = LibraryUiState.Loading

        if (query.trim().isBlank()) {
            _lookupUiState.value = LibraryUiState.Initial
            return
        }

        val result = client.lookup(query)
        when (result) {
            is NetworkResult.Success -> {
                val newLibrary = result.data
                _lookupUiState.value = LibraryUiState.Success(newLibrary, isRefreshing = false)
            }

            is NetworkResult.NetworkError -> {
                _lookupUiState.value = LibraryUiState.Error(
                    error = ErrorEvent(result.message ?: "Network error occurred"),
                    type = UiErrorType.Network
                )
            }

            is NetworkResult.HttpError -> {
                _lookupUiState.value = LibraryUiState.Error(
                    error = ErrorEvent(result.message ?: "Server error occurred"),
                    type = UiErrorType.Http
                )
            }

            is NetworkResult.UnexpectedError -> {
                _lookupUiState.value = LibraryUiState.Error(
                    error = ErrorEvent(result.cause.message ?: "An unexpected error occurred"),
                    type = UiErrorType.Unexpected
                )
            }
        }
    }

    override suspend fun addItem(item: T) {
        _addItemUiState.value = DetailsUiState.Loading
        val result = client.addItemToLibrary(item)

        when (result) {
            is NetworkResult.Success -> {
                _addItemUiState.value = DetailsUiState.Success(result.data)
            }
            else -> _addItemUiState.value = DetailsUiState.Error(
                error = ErrorEvent("An unexpected error occurred"),
                type = UiErrorType.Unexpected
            )
        }
    }

    override suspend fun command(payload: CommandPayload) {
        when(payload) {
            is CommandPayload.Movie -> handleSearchCommand(payload, payload.movieIds)
            is CommandPayload.Series -> handleSearchCommand(payload, listOf(payload.seriesId))
            else -> handleOtherCommand(payload)
        }
    }

    private suspend fun handleSearchCommand(payload: CommandPayload, ids: List<Long>) {
        _automaticSearchIds.value = ids
        val resp = client.command(payload)
        when(resp) {
            is NetworkResult.Success -> _automaticSearchResult.emit(true)
            is NetworkResult.NetworkError,
            is NetworkResult.HttpError,
            is NetworkResult.UnexpectedError -> _automaticSearchResult.emit(false)
        }
        _automaticSearchIds.value = emptyList()
    }

    private suspend fun handleOtherCommand(payload: CommandPayload) {
        client.command(payload)
    }

    override suspend fun getReleases(params: P) {
        _releasesUiState.emit(LibraryUiState.Loading)

        val resp = client.getReleases(params)
        when(resp) {
            is NetworkResult.Success -> {
                _releasesUiState.value = LibraryUiState.Success(resp.data)
            }
            is NetworkResult.NetworkError -> {
                _releasesUiState.value = LibraryUiState.Error(
                    error = ErrorEvent(resp.message ?: "Network error occurred"),
                    type = UiErrorType.Network
                )
            }
            is NetworkResult.HttpError -> {
                _releasesUiState.value = LibraryUiState.Error(
                    error = ErrorEvent(resp.message ?: "Server error occurred"),
                    type = UiErrorType.Http
                )
            }
            is NetworkResult.UnexpectedError -> {
                _releasesUiState.value = LibraryUiState.Error(
                    error = ErrorEvent(resp.cause.message ?: "An unexpected error occurred"),
                    type = UiErrorType.Unexpected
                )
            }
        }
    }

    override suspend fun fetchActivityTasksSync(instanceId: Long, page: Int, pageSize: Int): NetworkResult<QueuePage> {
        val resp = client.fetchActivityTasks(instanceId, page, pageSize)
        return resp
    }

    override suspend fun getItemHistory(id: Long, page: Int, pageSize: Int) {
        _itemHistoryRefreshing.value = true
        val resp = client.getItemHistory(id, page, pageSize)
        if (resp is NetworkResult.Success) {
            val newMap = _itemHistoryMap.value.toMutableMap()
            newMap[id] = resp.data
            _itemHistoryMap.value = newMap
        }
        _itemHistoryRefreshing.value = false
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
        val type: UiErrorType
    ): LibraryUiState<T>
}

sealed interface DetailsUiState<out T> {
    data object Initial: DetailsUiState<Nothing>
    data object Loading: DetailsUiState<Nothing>
    data class Success<T>(
        val item: T
    ): DetailsUiState<T>
    data class Error<T>(
        val error: ErrorEvent,
        val type: UiErrorType
    ): DetailsUiState<T>
}

data class ErrorEvent(
    val message: String,
    val timestamp: Long = getCurrentSystemTimeMillis()
)

enum class UiErrorType {
    Network,
    Http,
    Unexpected
}