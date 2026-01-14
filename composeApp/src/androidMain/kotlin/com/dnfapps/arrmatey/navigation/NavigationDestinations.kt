package com.dnfapps.arrmatey.navigation

import androidx.navigation3.runtime.NavKey
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.compose.utils.ReleaseFilterBy
import com.dnfapps.arrmatey.model.InstanceType

sealed interface HomeTab : NavKey {
    data object SeriesTab : HomeTab
    data object MoviesTab: HomeTab
    data object SettingsTab : HomeTab
}

sealed interface ArrScreen : NavKey {
    data object Library: ArrScreen
    data class Details(val id: Long): ArrScreen
    data class Preview<T>(val item: T): ArrScreen
    data class Search(val query: String = ""): ArrScreen
    data class MovieReleases(val movieId: Long): ArrScreen
    data class MovieFiles(val movie: ArrMovie): ArrScreen
    data class SeriesRelease(val seriesId: Long? = null, val seasonNumber: Int? = null, val episodeId: Long? = null): ArrScreen

}

sealed interface SettingsScreen : NavKey {
    data object Landing : SettingsScreen
    data object AddInstance : SettingsScreen
    data class EditInstance(val id: Long): SettingsScreen
    data object Dev: SettingsScreen
}