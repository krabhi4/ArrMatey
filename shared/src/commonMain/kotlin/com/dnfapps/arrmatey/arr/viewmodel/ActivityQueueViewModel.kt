package com.dnfapps.arrmatey.arr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.arr.api.model.Episode
import com.dnfapps.arrmatey.arr.api.model.QueueItem
import com.dnfapps.arrmatey.arr.api.model.RadarrQueueItem
import com.dnfapps.arrmatey.arr.api.model.SonarrQueueItem
import com.dnfapps.arrmatey.arr.usecase.GetActivityTasksUseCase
import com.dnfapps.arrmatey.arr.service.ActivityQueueService
import com.dnfapps.arrmatey.arr.state.ActivityQueueUiState
import com.dnfapps.arrmatey.compose.utils.QueueSortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.compose.utils.applySorting
import com.dnfapps.arrmatey.database.InstanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ActivityQueueViewModel(
    private val activityQueueService: ActivityQueueService,
    getActivityTasksUseCase: GetActivityTasksUseCase,
    instanceRepository: InstanceRepository
): ViewModel() {

    val activityTasks = getActivityTasksUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val tasksWithIssues = getActivityTasksUseCase.getTasksWithIssues()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val isPolling = activityQueueService.isPolling
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val instances = instanceRepository.observeAllInstances()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _activityQueueUiState = MutableStateFlow(ActivityQueueUiState())
    val activityQueueUiState: StateFlow<ActivityQueueUiState> = _activityQueueUiState.asStateFlow()

    val queueItems: StateFlow<List<QueueItem>> = combine(
        activityTasks,
        _activityQueueUiState
    ) { tasks, (instanceId, sortBy, sortOrder) ->
        tasks
            .groupByTask()
            .filterByInstance(instanceId)
            .applySorting(sortBy, sortOrder)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        startPolling()
    }

    fun startPolling() {
        activityQueueService.startPolling()
    }

    fun stopPolling() {
        activityQueueService.stopPolling()
    }

    fun setInstanceId(id: Long?) {
        val currentState = _activityQueueUiState.value
        _activityQueueUiState.value = currentState.copy(instanceId = id)
    }

    fun setSortBy(sortBy: QueueSortBy) {
        val currentState = _activityQueueUiState.value
        _activityQueueUiState.value = currentState.copy(sortBy = sortBy)
    }

    fun setSortOrder(order: SortOrder) {
        val currentState = _activityQueueUiState.value
        _activityQueueUiState.value = currentState.copy(sortOrder = order)
    }

    fun getQueueItemForEpisode(episode: Episode): SonarrQueueItem? {
        val tasks = activityTasks.value.filterIsInstance<SonarrQueueItem>()

        val episodeMatch = tasks.firstOrNull { it.calcEpisodeId == episode.id }
        if (episodeMatch != null) return episodeMatch

        return tasks.firstOrNull {
            it.calcSeriesId == episode.seriesId &&
                it.seasonNumber == episode.seasonNumber &&
                it.calcEpisodeId == null
        }
    }

    override fun onCleared() {
        super.onCleared()
        activityQueueService.stopPolling()
    }
}

private fun List<QueueItem>.groupByTask(): List<QueueItem> =
    groupBy { it.taskGroup }
        .map { (_, groupItems) ->
            val first = groupItems.first()
            groupItems.size.takeIf { it > 0 }?.let { size ->
                when (first) {
                    is SonarrQueueItem -> first.copy(taskGroupCount = size)
                    is RadarrQueueItem -> first.copy(taskGroupCount = size)
                }
            } ?: first
        }

private fun List<QueueItem>.filterByInstance(instanceId: Long?): List<QueueItem> =
    instanceId?.let { filter { it.instanceId == instanceId } } ?: this