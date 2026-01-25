package com.dnfapps.arrmatey.arr.state

import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.api.model.ArrRelease
import com.dnfapps.arrmatey.client.ErrorType
import com.dnfapps.arrmatey.datastore.InstancePreferences

sealed interface ArrLibrary {
    object Initial: ArrLibrary
    object Loading: ArrLibrary
    data class Success(
        val items: List<ArrMedia>,
        val preferences: InstancePreferences = InstancePreferences()
    ): ArrLibrary
    data class Error(
        val message: String,
        val type: ErrorType = ErrorType.Http
    ): ArrLibrary
}

sealed interface ReleaseLibrary {
    object Initial: ReleaseLibrary
    object Loading: ReleaseLibrary
    data class Success(
        val items: List<ArrRelease>
    ): ReleaseLibrary
    data class Error(
        val message: String,
        val type: ErrorType = ErrorType.Http
    ): ReleaseLibrary
}