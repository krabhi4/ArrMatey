package com.dnfapps.arrmatey.arr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.api.model.ArrMovie
import com.dnfapps.arrmatey.arr.api.model.ArrSeries
import com.dnfapps.arrmatey.arr.api.model.CommandPayload
import com.dnfapps.arrmatey.arr.api.model.Episode
import com.dnfapps.arrmatey.arr.api.model.HistoryItem
import com.dnfapps.arrmatey.arr.api.model.QualityProfile
import com.dnfapps.arrmatey.arr.api.model.Tag
import com.dnfapps.arrmatey.instances.repository.InstanceScopedRepository
import com.dnfapps.arrmatey.arr.state.MediaDetailsUiState
import com.dnfapps.arrmatey.instances.usecase.GetInstanceRepositoryUseCase
import com.dnfapps.arrmatey.arr.usecase.GetMediaDetailsUseCase
import com.dnfapps.arrmatey.client.NetworkResult
import com.dnfapps.arrmatey.client.OperationStatus
import com.dnfapps.arrmatey.client.onError
import com.dnfapps.arrmatey.client.onSuccess
import com.dnfapps.arrmatey.di.repositoryModule
import com.dnfapps.arrmatey.instances.model.InstanceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.emptySet

class ArrMediaDetailsViewModel(
    private val mediaId: Long,
    private val instanceType: InstanceType,
    private val getInstanceRepositoryUseCase: GetInstanceRepositoryUseCase,
    private val getMediaDetailsUseCase: GetMediaDetailsUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<MediaDetailsUiState>(MediaDetailsUiState.Initial)
    val uiState: StateFlow<MediaDetailsUiState> = _uiState.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _monitorStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val monitorStatus: StateFlow<OperationStatus> = _monitorStatus.asStateFlow()

    private val _isMonitored = MutableStateFlow(false)
    val isMonitored: StateFlow<Boolean> = _isMonitored.asStateFlow()

    private val _automaticSearchIds = MutableStateFlow<Set<Long>>(emptySet())
    val automaticSearchIds: StateFlow<Set<Long>> = _automaticSearchIds.asStateFlow()

    private val _lastSearchResult = MutableStateFlow<Boolean?>(null)
    val lastSearchResult: StateFlow<Boolean?> = _lastSearchResult.asStateFlow()

    private val _qualityProfiles = MutableStateFlow<List<QualityProfile>>(emptyList())
    val qualityProfiles: StateFlow<List<QualityProfile>> = _qualityProfiles.asStateFlow()

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags.asStateFlow()

    private val _deleteStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val deleteStatus: StateFlow<OperationStatus> = _deleteStatus.asStateFlow()

    private var currentRepository: InstanceScopedRepository? = null

    init {
        observeSelectedInstance()
    }

    private fun observeSelectedInstance() {
        viewModelScope.launch {
            getInstanceRepositoryUseCase.observeSelected(instanceType)
                .filterNotNull()
                .collectLatest { repository ->
                    currentRepository = repository
                    loadMediaDetails(repository)
                    observeMonitorStatus(repository)
                    observeQualityProfiles(repository)
                    observeTags(repository)
                }
        }
    }

    private suspend fun loadMediaDetails(repository: InstanceScopedRepository) {
        getMediaDetailsUseCase(mediaId, repository.instance.id)
            .collect { state ->
                _uiState.value = state
                if (state is MediaDetailsUiState.Success) {
                    _isMonitored.value = state.item.monitored
                }
            }

        repository.getItemHistory(mediaId)
            .onSuccess { _history.value = it }
    }

    private suspend fun observeMonitorStatus(repository: InstanceScopedRepository) {
        repository.monitorStatus.collect { status ->
            _monitorStatus.value = status
        }
    }

    private suspend fun observeQualityProfiles(repository: InstanceScopedRepository) {
        repository.qualityProfiles.collect { profiles ->
            _qualityProfiles.value = profiles
        }
    }

    private suspend fun observeTags(repository: InstanceScopedRepository) {
        repository.tags.collect { tags ->
            _tags.value = tags
        }
    }

    fun refreshDetails() {
        viewModelScope.launch {
            currentRepository?.let {
                loadMediaDetails(it)
            }
        }
    }

    fun toggleMonitored() {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            val state = _uiState.value as? MediaDetailsUiState.Success ?: return@launch

            val currentMonitored = state.item.monitored
            val updatedItem = when (val item = state.item) {
                is ArrSeries -> item.copy(monitored = !currentMonitored)
                is ArrMovie -> item.copy(monitored = !currentMonitored)
            }

            repository.updateMediaItem(updatedItem)
        }
    }

    fun toggleSeasonMonitored(seasonNumber: Int) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            if (instanceType != InstanceType.Sonarr) return@launch
            repository.toggleSeasonMonitor(mediaId, seasonNumber)
        }
    }

    fun toggleEpisodeMonitored(episode: Episode) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            if (instanceType != InstanceType.Sonarr) return@launch
            repository.toggleEpisodeMonitor(episode)
        }
    }

    private suspend fun performAutomaticSearch(
        id: Long,
        payload: CommandPayload,
        repository: InstanceScopedRepository
    ) {
        val currentIds = _automaticSearchIds.value.toMutableSet()
        currentIds.add(id)
        _automaticSearchIds.value = currentIds
        repository.executeCommand(payload)
            .onSuccess {
                _lastSearchResult.value = true
            }
            .onError { _, _, _ ->
                _lastSearchResult.value = false
            }
        val ids = _automaticSearchIds.value.toMutableSet()
        ids.remove(id)
        _automaticSearchIds.value = ids
        _lastSearchResult.value = null
    }

    fun performEpisodeAutomaticLookup(episodeId: Long) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            if (instanceType != InstanceType.Sonarr) return@launch
            val payload = CommandPayload.Episode(listOf(episodeId))
            performAutomaticSearch(episodeId, payload, repository)
        }
    }

    fun performSeasonAutomaticLookup(seasonNumber: Int) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            if (instanceType != InstanceType.Sonarr) return@launch
            val payload = CommandPayload.Season(mediaId, seasonNumber)
            performAutomaticSearch(mediaId, payload, repository)
        }
    }

    fun performMovieAutomaticLookup(movieId: Long) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            if (instanceType != InstanceType.Radarr) return@launch
            val payload = CommandPayload.Movie(movieIds = listOf(movieId))
            performAutomaticSearch(movieId, payload, repository)
        }
    }

    fun deleteMedia(deleteFiles: Boolean, addImportExclusion: Boolean) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            _deleteStatus.value = OperationStatus.InProgress

            repository.delete(mediaId, deleteFiles, addImportExclusion)
                .onSuccess {
                    _deleteStatus.value = OperationStatus.Success("Deleted successfully")
                }
                .onError { code, message, cause ->
                    _deleteStatus.value = OperationStatus.Error(code, message, cause)
                }
        }
    }

    fun resetMonitorStatus() {
        _monitorStatus.value = OperationStatus.Idle
    }
}