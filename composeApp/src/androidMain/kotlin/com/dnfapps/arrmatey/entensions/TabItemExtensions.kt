package com.dnfapps.arrmatey.entensions

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.api.client.ActivityQueue
import com.dnfapps.arrmatey.compose.TabItem

fun TabItem.stringResource() = when (this) {
    TabItem.SHOWS -> R.string.series
    TabItem.MOVIES -> R.string.movies
    TabItem.ACTIVITY -> R.string.activity
    TabItem.SETTINGS -> R.string.settings
}

@Composable
fun BadgeContent(tabItem: TabItem) {
    when (tabItem) {
        TabItem.ACTIVITY -> {
            val activityQueueIssuesCount by ActivityQueue.itemsWithIssues.collectAsStateWithLifecycle()
            if (activityQueueIssuesCount > 0) {
                Badge { Text(activityQueueIssuesCount.toString()) }
            }
        }
        else -> {}
    }
}