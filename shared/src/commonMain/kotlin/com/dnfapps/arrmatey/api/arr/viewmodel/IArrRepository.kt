package com.dnfapps.arrmatey.api.arr.viewmodel

import kotlinx.coroutines.flow.StateFlow

interface IArrRepository<T> {

    suspend fun refreshLibrary()
    suspend fun getDetails(id: Int)
    suspend fun setMonitorStatus(id: Int, monitorStatus: Boolean)

    val uiState: StateFlow<LibraryUiState<T>>
    val detailUiState: StateFlow<DetailsUiState<T>>

}