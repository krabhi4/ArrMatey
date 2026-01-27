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
import com.dnfapps.arrmatey.arr.api.model.RootFolder
import com.dnfapps.arrmatey.arr.api.model.Tag
import com.dnfapps.arrmatey.arr.state.MediaDetailsUiState
import com.dnfapps.arrmatey.arr.usecase.DeleteMediaUseCase
import com.dnfapps.arrmatey.arr.usecase.DeleteSeasonFilesUseCase
import com.dnfapps.arrmatey.arr.usecase.GetMediaDetailsUseCase
import com.dnfapps.arrmatey.arr.usecase.PerformAutomaticSearchUseCase
import com.dnfapps.arrmatey.arr.usecase.ToggleMonitorUseCase
import com.dnfapps.arrmatey.arr.usecase.UpdateMediaUseCase
import com.dnfapps.arrmatey.client.OperationStatus
import com.dnfapps.arrmatey.client.onError
import com.dnfapps.arrmatey.client.onSuccess
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.instances.repository.InstanceScopedRepository
import com.dnfapps.arrmatey.instances.usecase.GetInstanceRepositoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArrMediaDetailsViewModel(
    private val mediaId: Long,
    private val instanceType: InstanceType,
    private val getInstanceRepositoryUseCase: GetInstanceRepositoryUseCase,
    private val getMediaDetailsUseCase: GetMediaDetailsUseCase,
    private val toggleMonitorUseCase: ToggleMonitorUseCase,
    private val performAutomaticSearchUseCase: PerformAutomaticSearchUseCase,
    private val updateMediaUseCase: UpdateMediaUseCase,
    private val deleteMediaUseCase: DeleteMediaUseCase,
    private val deleteSeasonFilesUseCase: DeleteSeasonFilesUseCase
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

    private val _deleteStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val deleteStatus: StateFlow<OperationStatus> = _deleteStatus.asStateFlow()

    private val _deleteSeasonStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val deleteSeasonStatus: StateFlow<OperationStatus> = _deleteStatus.asStateFlow()


    private val _qualityProfiles = MutableStateFlow<List<QualityProfile>>(emptyList())
    val qualityProfiles: StateFlow<List<QualityProfile>> = _qualityProfiles.asStateFlow()

    private val _rootFolders = MutableStateFlow<List<RootFolder>>(emptyList())
    val rootFolders: StateFlow<List<RootFolder>> = _rootFolders.asStateFlow()

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags.asStateFlow()

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
                    loadData(repository)
                }
        }
    }

    private fun loadData(repository: InstanceScopedRepository) {
        viewModelScope.launch {
            getMediaDetailsUseCase(mediaId, repository.instance.id)
                .collect { state ->
                    _uiState.value = state
                    if (state is MediaDetailsUiState.Success) {
                        _isMonitored.value = state.item.monitored
                    }
                }
        }

        viewModelScope.launch {
            repository.getItemHistory(mediaId)
                .onSuccess { _history.value = it }
        }
        viewModelScope.launch {
            repository.monitorStatus.collect { status ->
                _monitorStatus.value = status
            }
        }
        viewModelScope.launch {
            repository.qualityProfiles.collect { profiles ->
                _qualityProfiles.value = profiles
            }
        }
        viewModelScope.launch {
            repository.rootFolders.collect { folders ->
                _rootFolders.value = folders
            }
        }
        viewModelScope.launch {
            repository.tags.collect { tags ->
                _tags.value = tags
            }
        }
    }

    fun refreshDetails() {
        currentRepository?.let {
            loadData(it)
        }
    }

    fun updateItem(item: ArrMedia) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            updateMediaUseCase(item, repository)
        }
    }

    fun toggleMonitored() {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            val item = (uiState.value as? MediaDetailsUiState.Success)?.item ?: return@launch
            toggleMonitorUseCase.toggleMedia(item, repository)
        }
    }

    fun toggleSeasonMonitored(seasonNumber: Int) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            toggleMonitorUseCase.toggleSeason(mediaId, seasonNumber, repository)
        }
    }

    fun toggleEpisodeMonitored(episode: Episode) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            toggleMonitorUseCase.toggleEpisode(episode, repository)
        }
    }

    fun deleteMedia(deleteFiles: Boolean, addImportExclusion: Boolean) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            deleteMediaUseCase(mediaId, deleteFiles, addImportExclusion, repository)
                .collect { status ->
                    _deleteStatus.value = status
                }
        }
    }

    fun deleteSeasonFiles(seasonNumber: Int) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            deleteSeasonFilesUseCase(mediaId, seasonNumber, repository)
                .collect { status ->
                    _deleteSeasonStatus.value = status
                }
        }
    }

    fun performEpisodeAutomaticLookup(episodeId: Long) {
        runSearch(episodeId, episodeId)
    }

    fun performSeasonAutomaticLookup(seasonNumber: Int) {
        runSearch(mediaId, seasonNumber = seasonNumber)
    }

    fun performMovieAutomaticLookup(movieId: Long) {
        runSearch(movieId)
    }

    private fun runSearch(
        trackingId: Long,
        episodeId: Long? = null,
        seasonNumber: Int? = null
    ) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            updateSearchIds(trackingId, add = true)

            performAutomaticSearchUseCase(mediaId, instanceType, repository, episodeId, seasonNumber)
                .onSuccess { _lastSearchResult.value = true }
                .onError { _, _, _ -> _lastSearchResult.value = false }

            updateSearchIds(trackingId, add = false)
            _lastSearchResult.value = null
        }
    }

    private fun updateSearchIds(id: Long, add: Boolean) {
        _automaticSearchIds.update { current ->
            if (add) current + id else current - id
        }
    }
}