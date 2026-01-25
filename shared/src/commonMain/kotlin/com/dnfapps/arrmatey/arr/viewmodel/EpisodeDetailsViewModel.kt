package com.dnfapps.arrmatey.arr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.arr.api.model.CommandPayload
import com.dnfapps.arrmatey.arr.api.model.Episode
import com.dnfapps.arrmatey.arr.api.model.HistoryItem
import com.dnfapps.arrmatey.arr.state.HistoryState
import com.dnfapps.arrmatey.instances.repository.InstanceScopedRepository
import com.dnfapps.arrmatey.instances.usecase.GetInstanceRepositoryUseCase
import com.dnfapps.arrmatey.client.NetworkResult
import com.dnfapps.arrmatey.client.OperationStatus
import com.dnfapps.arrmatey.client.onError
import com.dnfapps.arrmatey.client.onSuccess
import com.dnfapps.arrmatey.instances.model.InstanceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EpisodeDetailsViewModel(
    private val seriesId: Long,
    episode: Episode,
    private val getInstanceRepositoryUseCase: GetInstanceRepositoryUseCase
): ViewModel() {

    private val _episode = MutableStateFlow(episode)
    val episode: StateFlow<Episode> = _episode.asStateFlow()

    private val _history = MutableStateFlow<HistoryState>(HistoryState.Initial)
    val history: StateFlow<HistoryState> = _history.asStateFlow()

    private val _monitorStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val monitorStatus: StateFlow<OperationStatus> = _monitorStatus.asStateFlow()

    private var currentRepository: InstanceScopedRepository? = null

    init {
        observeSelectedInstance()
    }

    private fun observeSelectedInstance() {
        viewModelScope.launch {
            getInstanceRepositoryUseCase.observeSelected(InstanceType.Sonarr)
                .filterNotNull()
                .collectLatest { repository ->
                    currentRepository = repository
                    observeEpisode(repository)
                    loadHistory(repository)
                    observeMonitorStatus(repository)
                }
        }
    }

    private suspend fun observeEpisode(repository: InstanceScopedRepository) {
        repository.episodes
            .map { episodesMap ->
                episodesMap[seriesId]?.firstOrNull { it.id == episode.value.id }
            }
            .collect { episode ->
                episode?.let { _episode.value = it }
            }
    }

    private suspend fun loadHistory(repository: InstanceScopedRepository) {
        _history.value = HistoryState.Loading
        repository.getItemHistory(episode.value.id)
            .onSuccess { _history.value = HistoryState.Success(it) }
            .onError { _, message, _ ->
                _history.value = HistoryState.Error(message)
            }
    }

    private suspend fun observeMonitorStatus(repository: InstanceScopedRepository) {
        repository.monitorStatus.collect { status ->
            _monitorStatus.value = status
        }
    }

    fun toggleMonitor() {
        viewModelScope.launch {
            currentRepository?.toggleEpisodeMonitor(episode.value)
        }
    }

    fun executeAutomaticSearch() {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            val payload = CommandPayload.Episode(listOf(episode.value.id))
            repository.executeCommand(payload)
        }
    }

    fun refreshHistory() {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            _history.value = HistoryState.Loading
            repository.getItemHistory(episode.value.id)
                .onSuccess { _history.value = HistoryState.Success(it) }
                .onError { _, message, _ ->
                    HistoryState.Error(message)
                }
        }
    }

    fun resetMonitorStatus() {
        _monitorStatus.value = OperationStatus.Idle
    }

}