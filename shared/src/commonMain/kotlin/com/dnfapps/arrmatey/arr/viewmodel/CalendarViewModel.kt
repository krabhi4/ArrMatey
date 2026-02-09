package com.dnfapps.arrmatey.arr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.arr.api.model.ArrMovie
import com.dnfapps.arrmatey.arr.api.model.Episode
import com.dnfapps.arrmatey.arr.api.model.EpisodeGroup
import com.dnfapps.arrmatey.arr.state.CalendarFilterState
import com.dnfapps.arrmatey.arr.state.CalendarState
import com.dnfapps.arrmatey.arr.state.ContentFilter
import com.dnfapps.arrmatey.arr.usecase.GetCalendarUseCase
import com.dnfapps.arrmatey.database.InstanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class CalendarViewModel(
    private val getCalendarUseCase: GetCalendarUseCase,
    instanceRepository: InstanceRepository
) : ViewModel() {

    private val _filterState = MutableStateFlow(CalendarFilterState())
    val filterState: StateFlow<CalendarFilterState> = _filterState.asStateFlow()

    val calendarState = combine(
        getCalendarUseCase(),
        _filterState
    ) { calendar, filter ->
        CalendarState(
            movies = filterMovies(calendar.movies, filter),
            episodes = filterEpisodes(calendar.episodes, filter),
            groupedEpisodes = filterEpisodeGroups(calendar.groupedEpisodes, filter),
            dates = calendar.dates,
            isLoading = calendar.isLoading,
            isLoadingFuture = calendar.isLoadingFuture,
            error = calendar.error,
            today = calendar.today
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarState()
    )

    val instances = instanceRepository.observeAllInstances()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            getCalendarUseCase.load()
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            getCalendarUseCase.loadMore()
        }
    }

    fun reset() {
        getCalendarUseCase.reset()
    }

    fun setContentFilter(contentFilter: ContentFilter) {
        _filterState.update {
            it.copy(contentFilter = contentFilter)
        }
    }

    fun toggleShowMonitoredOnly() {
        _filterState.update {
            it.copy(showMonitoredOnly = !it.showMonitoredOnly)
        }
    }

    fun toggleShowPremiersOnly() {
        val current = _filterState.value.showPremiersOnly
        _filterState.update {
            it.copy(showPremiersOnly = !current, showFinalesOnly = if (!current) false else it.showFinalesOnly)
        }
    }

    fun toggleShowFinalesOnly() {
        val current = _filterState.value.showFinalesOnly
        _filterState.update {
            it.copy(showFinalesOnly = !current, showPremiersOnly = if (!current) false else it.showPremiersOnly)
        }
    }

    fun setFilterInstanceId(id: Long?) {
        _filterState.update {
            it.copy(instanceId = id)
        }
    }

    private fun filterMovies(
        movieMap: Map<LocalDate, List<ArrMovie>>,
        filter: CalendarFilterState
    ): Map<LocalDate, List<ArrMovie>> =
        if (filter.contentFilter == ContentFilter.EpisodesOnly || filter.showFinalesOnly) {
            emptyMap()
        } else {
            movieMap.mapValues { (_, movies) ->
                movies.filter { movie ->
                    filterMovie(movie, filter)
                }
            }
        }

    private fun filterEpisodes(
        episodesMap: Map<LocalDate, List<Episode>>,
        filter: CalendarFilterState
    ): Map<LocalDate, List<Episode>> =
        if (filter.contentFilter == ContentFilter.MoviesOnly) {
            emptyMap()
        } else {
            episodesMap.mapValues { (_, episodes) ->
                episodes.filter { episode ->
                    filterEpisode(episode, filter)
                }
            }
        }

    private fun filterEpisodeGroups(
        episodeGroups: Map<LocalDate, List<EpisodeGroup>>,
        filter: CalendarFilterState
    ): Map<LocalDate, List<EpisodeGroup>> =
        if (filter.contentFilter == ContentFilter.MoviesOnly) {
            emptyMap()
        } else {
            episodeGroups.mapValues { (_, groups) ->
                groups.mapNotNull { group ->
                    val allEpisodes = listOf(group.first) + group.additional
                    val filteredEpisodes = allEpisodes.filter { episode ->
                        filterEpisode(episode, filter)
                    }
                    if (filteredEpisodes.isNotEmpty()) {
                        EpisodeGroup(
                            first = filteredEpisodes.first(),
                            additional = filteredEpisodes.drop(1)
                        )
                    } else {
                        null
                    }
                }
            }
        }

    private fun filterMovie(movie: ArrMovie, filter: CalendarFilterState): Boolean {
        return (!filter.showMonitoredOnly || movie.monitored) &&
                (filter.instanceId == null || movie.instanceId == filter.instanceId)
    }

    private fun filterEpisode(episode: Episode, filter: CalendarFilterState): Boolean {
        return (!filter.showMonitoredOnly || episode.monitored) &&
                (!filter.showPremiersOnly || (episode.seasonNumber == 1 && episode.episodeNumber == 1)) &&
                (!filter.showFinalesOnly || episode.finaleType != null) &&
                (filter.instanceId == null || episode.instanceId == filter.instanceId)
    }
}