package com.dnfapps.arrmatey.arr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.state.LibraryUiState
import com.dnfapps.arrmatey.arr.usecase.GetLibraryUseCase
import com.dnfapps.arrmatey.client.ErrorType
import com.dnfapps.arrmatey.client.OperationStatus
import com.dnfapps.arrmatey.compose.utils.FilterBy
import com.dnfapps.arrmatey.compose.utils.SortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.datastore.InstancePreferences
import com.dnfapps.arrmatey.instances.model.InstanceData
import com.dnfapps.arrmatey.instances.model.InstanceType
import com.dnfapps.arrmatey.instances.repository.InstanceScopedRepository
import com.dnfapps.arrmatey.instances.usecase.GetInstanceRepositoryUseCase
import com.dnfapps.arrmatey.instances.usecase.UpdatePreferencesUseCase
import com.dnfapps.arrmatey.ui.theme.ViewType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ArrMediaViewModel(
    private val instanceType: InstanceType,
    private val getInstanceRepositoryUseCase: GetInstanceRepositoryUseCase,
    private val getLibraryUseCase: GetLibraryUseCase,
    private val updatePreferencesUseCase: UpdatePreferencesUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState<ArrMedia>>(LibraryUiState.Initial)
    val uiState: StateFlow<LibraryUiState<ArrMedia>> = _uiState.asStateFlow()

    private val _instanceData = MutableStateFlow<InstanceData?>(null)
    val instanceData: StateFlow<InstanceData?> = _instanceData.asStateFlow()

    private val _addItemStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val addItemStatus: StateFlow<OperationStatus> = _addItemStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _preferences = MutableStateFlow(InstancePreferences())
    val preferences: StateFlow<InstancePreferences> = _preferences.asStateFlow()

    private val _hasServerConnectivityError = MutableStateFlow(false)
    val hasServerConnectivityError: StateFlow<Boolean> = _hasServerConnectivityError.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

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

                    observeInstanceData(repository)
                    observeLibrary(repository.instance.id)

                    repository.refreshAllMetadata()
                }
        }
    }

    private fun observeLibrary(instanceId: Long) {
        getLibraryUseCase(instanceId)
            .combine(_searchQuery) { state, query ->
                when (state) {
                    is LibraryUiState.Success<ArrMedia> -> {
                        _preferences.value = state.preferences
                        filterSuccessState(state, query)
                    }
                    is LibraryUiState.Error -> {
                        handleErrorState(state)
                        state
                    }
                    else -> state
                }
            }
            .onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    private fun filterSuccessState(state: LibraryUiState.Success<ArrMedia>, query: String) =
        state.copy(
            items = state.items.filter {
                it.sortTitle?.contains(query, ignoreCase = true) == true
            }
        )

    private fun handleErrorState(state: LibraryUiState.Error) {
        _errorMessage.value = state.message
        _hasServerConnectivityError.value = (state.type == ErrorType.Network)
    }

    private fun observeInstanceData(repository: InstanceScopedRepository) {
        viewModelScope.launch {
            combine(
                repository.qualityProfiles,
                repository.rootFolders,
                repository.tags,
            ) { profiles, folders, tags ->
                InstanceData(
                    qualityProfiles = profiles,
                    rootFolders = folders,
                    tags = tags
                )
            }.collect { data ->
                _instanceData.value = data
            }
        }
    }

    fun executeAutomaticSearch(seriesId: Long) {
        viewModelScope.launch {
            currentRepository?.executeAutomaticSearch(seriesId)
        }
    }

    fun updateViewType(viewType: ViewType) {
        safeSavePreference { it.copy(viewType = viewType) }
    }

    fun updateSortBy(sortBy: SortBy) {
        safeSavePreference { it.copy(sortBy = sortBy) }
    }

    fun updateSortOrder(sortOrder: SortOrder) {
        safeSavePreference { it.copy(sortOrder = sortOrder) }
    }

    fun updateFilterBy(filterBy: FilterBy) {
        safeSavePreference { it.copy(filterBy = filterBy) }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun safeSavePreference(transform: (InstancePreferences) -> InstancePreferences) {
        viewModelScope.launch {
            val repository = currentRepository ?: return@launch
            val currentState = _uiState.value as? LibraryUiState.Success ?: return@launch
            val preferences = currentState.preferences

            val updatedPreferences = transform(preferences)
            updatePreferencesUseCase.savePreferences(repository.instance.id, updatedPreferences)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            currentRepository?.refreshLibrary()
        }
    }
}