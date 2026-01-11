package com.dnfapps.arrmatey.api.arr.viewmodel

import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.CommandPayload
import com.dnfapps.arrmatey.api.arr.model.IArrRelease
import com.dnfapps.arrmatey.api.arr.model.QualityProfile
import com.dnfapps.arrmatey.api.arr.model.QueuePage
import com.dnfapps.arrmatey.api.arr.model.ReleaseParams
import com.dnfapps.arrmatey.api.arr.model.RootFolder
import com.dnfapps.arrmatey.api.arr.model.Tag
import com.dnfapps.arrmatey.api.client.NetworkResult
import kotlinx.coroutines.flow.StateFlow

interface IArrRepository<T: AnyArrMedia, R: IArrRelease, P: ReleaseParams> {

    suspend fun refreshLibrary()
    suspend fun getDetails(id: Int)
    suspend fun setMonitorStatus(id: Int, monitorStatus: Boolean)
    suspend fun lookup(query: String)
    suspend fun addItem(item: T)
    suspend fun command(payload: CommandPayload)
    suspend fun getReleases(params: P)
    suspend fun downloadRelease(release: R, force: Boolean = false)

    suspend fun fetchActivityTasksSync(instanceId: Long, pageSize: Int = 100): NetworkResult<QueuePage>

    val uiState: StateFlow<LibraryUiState<T>>
    val detailUiState: StateFlow<DetailsUiState<T>>
    val lookupUiState: StateFlow<LibraryUiState<T>>
    val addItemUiState: StateFlow<DetailsUiState<T>>
    val releasesUiState: StateFlow<LibraryUiState<R>>
    val downloadReleaseState: StateFlow<DownloadState>

    val automaticSearchIds: StateFlow<List<Int>>
    val automaticSearchResult: StateFlow<Boolean?>

    val qualityProfiles: StateFlow<List<QualityProfile>>
    val rootFolders: StateFlow<List<RootFolder>>
    val tags: StateFlow<List<Tag>>

}

sealed interface DownloadState {
    data object Initial: DownloadState
    data class Loading(val guid: String): DownloadState
    data object Success: DownloadState
    data object Error: DownloadState
}