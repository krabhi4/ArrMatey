package com.dnfapps.arrmatey.arr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.arr.api.model.ArrRelease
import com.dnfapps.arrmatey.arr.api.model.MovieRelease
import com.dnfapps.arrmatey.arr.api.model.ReleaseParams
import com.dnfapps.arrmatey.arr.api.model.SeriesRelease
import com.dnfapps.arrmatey.arr.state.DownloadState
import com.dnfapps.arrmatey.arr.state.InteractiveSearchUiState
import com.dnfapps.arrmatey.arr.state.LibraryUiState
import com.dnfapps.arrmatey.arr.usecase.DownloadReleaseUseCase
import com.dnfapps.arrmatey.arr.usecase.GetReleasesUseCase
import com.dnfapps.arrmatey.compose.utils.ReleaseFilterBy
import com.dnfapps.arrmatey.compose.utils.ReleaseSortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.instances.usecase.GetInstanceRepositoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InteractiveSearchViewModel(
    private val instanceType: InstanceType,
    defaultFilterBy: ReleaseFilterBy,
    private val getReleasesUseCase: GetReleasesUseCase,
    private val downloadReleaseUseCase: DownloadReleaseUseCase,
    private val getInstanceRepositoryUseCase: GetInstanceRepositoryUseCase
): ViewModel() {

    private val _releaseUiState = MutableStateFlow<LibraryUiState<ArrRelease>>(LibraryUiState.Initial)
    val releaseUiState: StateFlow<LibraryUiState<ArrRelease>> = _releaseUiState.asStateFlow()

    private val _downloadReleaseState = MutableStateFlow<DownloadState>(DownloadState.Initial)
    val downloadReleaseState: StateFlow<DownloadState> = _downloadReleaseState.asStateFlow()

    private val _filterUiState = MutableStateFlow(InteractiveSearchUiState(filterBy = defaultFilterBy))
    val filterUiState: StateFlow<InteractiveSearchUiState> = _filterUiState.asStateFlow()

    private val _downloadStatus = MutableStateFlow<Boolean?>(null)
    val downloadStatus: StateFlow<Boolean?> = _downloadStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        observeReleases()
        observeDownloadStatus()
    }

    private fun observeReleases() {
        viewModelScope.launch {
            combine(
                getReleasesUseCase(instanceType),
                _filterUiState,
                _searchQuery
            ) { release, filter, query ->
                when (release) {
                    is LibraryUiState.Success -> {
                        val sorted = applySorting(release.items, filter)
                        val filtered = applyFiltering(sorted, filter, query)
                        release.copy(items = filtered)
                    }
                    else -> release
                }
            }.collect { state ->
                _releaseUiState.value = state
            }
        }
    }

    private fun applySorting(
        items: List<ArrRelease>,
        filter: InteractiveSearchUiState
    ): List<ArrRelease> {
        val comparator: Comparator<ArrRelease> = when (filter.sortBy) {
            ReleaseSortBy.Weight -> compareBy { it.releaseWeight }
            ReleaseSortBy.Age -> compareBy { it.ageMinutes }
            ReleaseSortBy.Quality -> compareBy { it.quality.qualityLabel }
            ReleaseSortBy.Seeders -> compareBy { it.seeders }
            ReleaseSortBy.FileSize -> compareBy { it.size }
            ReleaseSortBy.CustomScore -> compareBy { it.customFormatScore }
        }
        return if (filter.sortOrder == SortOrder.Asc) {
            items.sortedWith(comparator)
        } else {
            items.sortedWith(comparator.reversed())
        }
    }

    private inline fun <reified T :ArrRelease> applyFiltering(
        items: List<T>,
        filter: InteractiveSearchUiState,
        query: String
    ): List<T> {
        if (items.isEmpty()) return items

        return when {
            T::class == SeriesRelease::class -> {
                @Suppress("UNCHECKED_CAST")
                seriesFiltering(items as List<SeriesRelease>, filter, query) as List<T>
            }
            T::class == MovieRelease::class -> {
                @Suppress("UNCHECKED_CAST")
                movieFiltering(items as List<MovieRelease>, query) as List<T>
            }
            else -> items
        }
    }

    private fun seriesFiltering(
        items: List<SeriesRelease>,
        filter: InteractiveSearchUiState,
        query: String
    ): List<SeriesRelease> = when (filter.filterBy) {
        ReleaseFilterBy.Any -> items
        ReleaseFilterBy.SeasonPack -> items.filter { it.fullSeason }
        ReleaseFilterBy.SingleEpisode -> items.filter { !it.fullSeason }
    }.filter {
        it.title.contains(query, ignoreCase = true)
    }

    private fun movieFiltering(
        items: List<MovieRelease>,
        query: String
    ): List<MovieRelease> = items.filter {
        it.title.contains(query, ignoreCase = true)
    }

    private fun observeDownloadStatus() {
        viewModelScope.launch {
            getInstanceRepositoryUseCase.observeSelected(instanceType)
                .filterNotNull()
                .collectLatest { repository ->
                    repository.downloadStatus.collect { status ->
                        _downloadReleaseState.value = status
                        _downloadStatus.value = when (status) {
                            is DownloadState.Success -> true
                            is DownloadState.Error -> false
                            else -> null
                        }
                    }
                }
        }
    }

    fun getRelease(params: ReleaseParams) {
        viewModelScope.launch {
            getReleasesUseCase.fetch(instanceType, params)
        }
    }

    fun downloadRelease(release: ArrRelease, force: Boolean = false) {
        viewModelScope.launch {
            _downloadReleaseState.value = DownloadState.Loading(release.guid)
            downloadReleaseUseCase(instanceType, release, force)
        }
    }

    fun resetDownloadState() {
        _downloadReleaseState.value = DownloadState.Initial
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortBy(sortBy: ReleaseSortBy) {
        _filterUiState.update {
            it.copy(sortBy = sortBy)
        }
    }

    fun setSortOrder(sortOrder: SortOrder) {
        _filterUiState.update {
            it.copy(sortOrder = sortOrder)
        }
    }

    fun setFilterBy(filterBy: ReleaseFilterBy) {
        _filterUiState.update {
            it.copy(filterBy = filterBy)
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            getReleasesUseCase.clear(instanceType)
        }
        super.onCleared()
    }
}