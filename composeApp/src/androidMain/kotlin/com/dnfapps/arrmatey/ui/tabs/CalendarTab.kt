package com.dnfapps.arrmatey.ui.tabs

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.dnfapps.arrmatey.ui.menu.CalendarFilterMenu
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(mokoString(MR.strings.schedule)) },
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

                    CalendarFilterMenu(
                        instances = instances,
                        filterState = filterState,
                        onInstanceChanged = { viewModel.setFilterInstanceId(it) },
                        onContentFilterChanged = { viewModel.setContentFilter(it) },
                        onToggleFilterMonitored = { viewModel.toggleShowMonitoredOnly() },
                        onToggleFilterPremiersOnly = { viewModel.toggleShowPremiersOnly() },
                        onToggleFilterFinalesOnly = { viewModel.toggleShowFinalesOnly() },
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { paddingValues ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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