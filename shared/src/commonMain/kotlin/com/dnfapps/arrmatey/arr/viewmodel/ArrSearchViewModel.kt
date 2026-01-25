package com.dnfapps.arrmatey.arr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.arr.state.ArrLibrary
import com.dnfapps.arrmatey.arr.usecase.GetLookupResultsUseCase
import com.dnfapps.arrmatey.arr.usecase.PerformLookupUseCase
import com.dnfapps.arrmatey.compose.utils.SortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.instances.model.InstanceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArrSearchViewModel(
    private val instanceType: InstanceType,
    private val getLookupResultsUseCase: GetLookupResultsUseCase,
    private val performLookupUseCase: PerformLookupUseCase
): ViewModel() {

    private val _lookupUiState = MutableStateFlow<ArrLibrary>(ArrLibrary.Initial)

    private val _sortBy = MutableStateFlow(SortBy.Relevance)
    val sortBy: StateFlow<SortBy> = _sortBy.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.Asc)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    val lookupUiState: StateFlow<ArrLibrary> = combine(
        _lookupUiState,
        _sortBy,
        _sortOrder
    ) { state, sortBy, sortOrder ->
        when (state) {
            is ArrLibrary.Success -> {
                val sorted = when (sortBy) {
                    SortBy.Relevance -> state.items
                    SortBy.Year -> state.items.sortedBy { it.year }
                    SortBy.Rating -> state.items.sortedBy { it.ratingScore() }
                    else -> state.items
                }
                val finalList = if (sortOrder == SortOrder.Desc) sorted.reversed() else sorted
                ArrLibrary.Success(items = finalList, preferences = state.preferences)
            }
            else -> state
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ArrLibrary.Initial
    )

    init {
        observeLookupResults()
    }

    private fun observeLookupResults() {
        viewModelScope.launch {
            getLookupResultsUseCase(instanceType)
                .collect { state ->
                    _lookupUiState.value = state
                }
        }
    }

    fun performLookup(query: String) {
        viewModelScope.launch {
            performLookupUseCase(instanceType, query)
        }
    }

    fun setSortBy(sortBy: SortBy) {
        _sortBy.value = sortBy
    }

    fun setSortOrder(sortOrder: SortOrder) {
        _sortOrder.value = sortOrder
    }

    fun clearLookup() {
        viewModelScope.launch {
            performLookupUseCase.clear(instanceType)
        }
    }

    override fun onCleared() {
        clearLookup()
        super.onCleared()
    }
}