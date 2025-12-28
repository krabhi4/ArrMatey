package com.dnfapps.arrmatey.api.arr.viewmodel

import com.dnfapps.arrmatey.api.arr.model.QualityProfile
import com.dnfapps.arrmatey.api.arr.model.RootFolder
import com.dnfapps.arrmatey.api.arr.model.Tag
import kotlinx.coroutines.flow.StateFlow

interface IArrRepository<T> {

    suspend fun refreshLibrary()
    suspend fun getDetails(id: Int)
    suspend fun setMonitorStatus(id: Int, monitorStatus: Boolean)
    suspend fun lookup(query: String)
    suspend fun addItem(item: T)

    val uiState: StateFlow<LibraryUiState<T>>
    val detailUiState: StateFlow<DetailsUiState<T>>
    val lookupUiState: StateFlow<LibraryUiState<T>>
    val addItemUiState: StateFlow<DetailsUiState<T>>

    val qualityProfiles: StateFlow<List<QualityProfile>>
    val rootFolders: StateFlow<List<RootFolder>>
    val tags: StateFlow<List<Tag>>

}