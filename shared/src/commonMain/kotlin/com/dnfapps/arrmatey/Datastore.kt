@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.dnfapps.arrmatey

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.dnfapps.arrmatey.compose.utils.FilterBy
import com.dnfapps.arrmatey.compose.utils.SortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.ui.theme.ViewType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath {
        producePath().toPath()
    }

internal const val dataStoreFileName = "arrmatey.preferences_pb"

expect class DataStoreFactory() {
    fun provideDataStore(): DataStore<Preferences>
}

class PreferencesStore(): KoinComponent {
    private val dataStore: DataStore<Preferences> by inject()

    private val SORT_BY_KEY = intPreferencesKey("sortBy")
    private val SORT_ORDER_KEY = intPreferencesKey("sortOrder")
    private val FILTER_BY_KEY = intPreferencesKey("filterBy")
    private val SONARR_INFO_CARD_KEY = booleanPreferencesKey("sonarrInfoCard")
    private val RADARR_INFO_CARD_KEY = booleanPreferencesKey("radarrInfoCard")
    private val SONARR_VIEW_TYPE_KEY = intPreferencesKey("sonarrViewType")
    private val RADARR_VIEW_TYPE_KEY = intPreferencesKey("radarrViewType")

    private fun infoCardKey(type: InstanceType): Preferences.Key<Boolean> = when (type) {
        InstanceType.Sonarr -> SONARR_INFO_CARD_KEY
        InstanceType.Radarr -> RADARR_INFO_CARD_KEY
    }

    private fun viewTypeKey(type: InstanceType): Preferences.Key<Int> = when (type) {
        InstanceType.Sonarr -> SONARR_VIEW_TYPE_KEY
        InstanceType.Radarr -> RADARR_VIEW_TYPE_KEY
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    val sortBy: Flow<SortBy> = dataStore.data
        .map { preferences -> SortBy.entries[preferences[SORT_BY_KEY] ?: 0] }

    fun saveSortBy(sortBy: SortBy) {
        scope.launch {
            dataStore.edit { preferences ->
                preferences[SORT_BY_KEY] = sortBy.ordinal
            }
        }
    }

    val sortOrder: Flow<SortOrder> = dataStore.data
        .map { preferences -> SortOrder.entries[preferences[SORT_ORDER_KEY] ?: 0] }

    fun saveSortOrder(sortOrder: SortOrder) {
        scope.launch {
            dataStore.edit { preferences ->
                preferences[SORT_ORDER_KEY] = sortOrder.ordinal
            }
        }
    }

    val filterBy: Flow<FilterBy> = dataStore.data
        .map { preferences -> FilterBy.entries[preferences[FILTER_BY_KEY] ?: 0] }

    fun saveFilterBy(filterBy: FilterBy) {
        scope.launch {
            dataStore.edit { preferences ->
                preferences[FILTER_BY_KEY] = filterBy.ordinal
            }
        }
    }

    val showInfoCards: Flow<Map<InstanceType, Boolean>> = dataStore.data
        .map { preferences ->
            InstanceType.entries.associateWith { type -> (preferences[infoCardKey(type)] ?: true) }
        }

    fun dismissInfoCard(type: InstanceType) {
        setInfoCardVisibility(type, false)
    }

    fun setInfoCardVisibility(type: InstanceType, value: Boolean) {
        scope.launch {
            dataStore.edit { preferences ->
                preferences[infoCardKey(type)] = value
            }
        }
    }

    val viewType: Flow<Map<InstanceType, ViewType>> = dataStore.data
        .map { preferences ->
            InstanceType.entries.associateWith { type ->
                ViewType.entries[preferences[viewTypeKey(type)] ?: 0]
            }
        }

    fun saveViewType(instanceType: InstanceType, viewType: ViewType) {
        scope.launch {
            dataStore.edit { preferences ->
                preferences[viewTypeKey(type = instanceType)] = viewType.ordinal
            }
        }
    }
}