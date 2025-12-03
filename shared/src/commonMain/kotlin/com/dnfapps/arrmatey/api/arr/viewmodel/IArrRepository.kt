package com.dnfapps.arrmatey.api.arr.viewmodel

import kotlinx.coroutines.flow.StateFlow

interface IArrRepository<T> {

    suspend fun refreshLibrary()

    val uiState: StateFlow<LibraryUiState<T>>

}