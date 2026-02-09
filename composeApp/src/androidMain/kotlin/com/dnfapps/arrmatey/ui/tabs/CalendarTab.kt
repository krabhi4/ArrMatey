package com.dnfapps.arrmatey.ui.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.arr.state.CalendarViewMode
import com.dnfapps.arrmatey.arr.viewmodel.CalendarViewModel
import com.dnfapps.arrmatey.datastore.PreferencesStore
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.shared.MR
import com.dnfapps.arrmatey.ui.calendar.CalendarListView
import com.dnfapps.arrmatey.ui.calendar.CalendarMonthView
import com.dnfapps.arrmatey.ui.calendar.FilterMenu
import com.dnfapps.arrmatey.utils.mokoString
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTab(
    viewModel: CalendarViewModel = koinInject(),
    preferencesStore: PreferencesStore = koinInject()
) {
    val calendarState by viewModel.calendarState.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val viewMode by preferencesStore.calendarViewMode.collectAsStateWithLifecycle(CalendarViewMode.List)

    val instances by viewModel.instances.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mokoString(MR.strings.release_schedule)) },
                actions = {
                    IconButton(onClick = {
                        preferencesStore.toggleCalendarViewMode()
                    }) {
                        Icon(
                            imageVector = when (viewMode) {
                                CalendarViewMode.List -> Icons.Default.CalendarMonth
                                CalendarViewMode.Month -> Icons.Default.CalendarViewDay
                            },
                            contentDescription = null
                        )
                    }

                    Box {
                        IconButton(onClick = {
                            showFilterSheet = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null
                            )
                        }
                        FilterMenu(
                            expanded = showFilterSheet,
                            onDismiss = { showFilterSheet = false },
                            instances = instances,
                            filterState = filterState,
                            onInstanceChanged = { viewModel.setFilterInstanceId(it) },
                            onContentFilterChanged = { viewModel.setContentFilter(it) },
                            onToggleFilterMonitored = { viewModel.toggleShowMonitoredOnly() },
                            onToggleFilterPremiersOnly = { viewModel.toggleShowPremiersOnly() },
                            onToggleFilterFinalesOnly = { viewModel.toggleShowFinalesOnly() },
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues.copy(bottom = 0.dp)),
            isRefreshing = calendarState.isLoading,
            onRefresh = { viewModel.load() }
        ) {
            when (viewMode) {
                CalendarViewMode.List -> {
                    CalendarListView(
                        state = calendarState,
                        onLoadMore = { viewModel.loadMore() }
                    )
                }
                CalendarViewMode.Month -> {
                    CalendarMonthView(calendarState)
                }
            }
        }
    }
}