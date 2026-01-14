package com.dnfapps.arrmatey.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.CommandPayload
import com.dnfapps.arrmatey.api.arr.model.IArrRelease
import com.dnfapps.arrmatey.api.arr.model.ReleaseParams
import com.dnfapps.arrmatey.api.arr.viewmodel.BaseArrRepository
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

abstract class ArrViewModel(protected val instance: Instance): ViewModel(), KoinComponent {
    protected val repository: BaseArrRepository<AnyArrMedia, IArrRelease, ReleaseParams> by inject {
        parametersOf(
            instance
        )
    }

    val uiState = repository.uiState
    val detailsUiState = repository.detailUiState
    val lookupUiState = repository.lookupUiState
    val addItemUiState = repository.addItemUiState
    val releaseUiState = repository.releasesUiState
    val downloadReleaseState = repository.downloadReleaseState

    val automaticSearchIds = repository.automaticSearchIds
    val automaticSearchResult = repository.automaticSearchResult

    val itemHistoryMap = repository.itemHistoryMap
    val itemHistoryRefreshing = repository.itemHistoryRefreshing

    val qualityProfiles = repository.qualityProfiles
    val rootFolders = repository.rootFolders
    val tags = repository.tags

    fun refreshLibrary() {
        viewModelScope.launch {
            repository.refreshLibrary()
        }
    }

    open fun getDetails(id: Long) {
        viewModelScope.launch {
            repository.getDetails(id)
        }
    }

    fun setMonitorStatus(id: Long, monitorStatus: Boolean) {
        viewModelScope.launch {
            repository.setMonitorStatus(id, monitorStatus)
        }
    }

    fun performLookup(query: String) {
        viewModelScope.launch {
            repository.lookup(query)
        }
    }

    fun command(payload: CommandPayload) {
        viewModelScope.launch {
            repository.command(payload)
        }
    }

    fun getReleases(params: ReleaseParams) {
        viewModelScope.launch {
            repository.getReleases(params)
        }
    }

    fun downloadRelease(release: IArrRelease, force: Boolean = false) {
        viewModelScope.launch {
            repository.downloadRelease(release, force)
        }
    }

    fun <T: AnyArrMedia> addItem(item: T) {
        viewModelScope.launch {
            repository.addItem(item)
        }
    }

    fun getItemHistory(id: Long, page: Int = 1, pageSize: Int = 100) {
        viewModelScope.launch {
            repository.getItemHistory(id, page, pageSize)
        }
    }
}

class ArrViewModelFactory(
    private val instance: Instance
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArrViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return when (instance.type) {
                InstanceType.Sonarr -> SonarrViewModel(instance) as T
                InstanceType.Radarr -> RadarrViewModel(instance) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun rememberArrViewModel(instance: Instance?): ArrViewModel? {
    return instance?.let { instance ->
        viewModel<ArrViewModel>(
            key = instance.id.toString(),
            factory = ArrViewModelFactory(instance)
        )
    }
}