package com.dnfapps.arrmatey.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.arr.api.model.Episode
import com.dnfapps.arrmatey.arr.api.model.Season
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.entensions.Bullet
import com.dnfapps.arrmatey.extensions.formatAsRuntime
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.Navigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun SeasonHeader(
    seriesId: Long?,
    season: Season,
    episodes: List<Episode>,
    onPerformAutomaticSearch: (Int) -> Unit,
    searchInProgress: (Int) -> Boolean,
    onDeleteSeason: () -> Unit,
    deleteInProgress: Boolean,
    navigationManager: NavigationManager = koinInject(),
    navigation: Navigation<ArrScreen> = navigationManager.series()
) {
    val tbaLabel = stringResource(R.string.tba)
    val year = remember(episodes) {
        episodes.mapNotNull { it.airDateUtc }.minOrNull()
            ?.toLocalDateTime(TimeZone.UTC)?.date?.year?.toString()
            ?: tbaLabel
    }

    val runtime = remember(episodes) {
        val items = episodes.mapNotNull { it.runtime?.takeIf { r -> r > 0 } }
        if (items.isEmpty()) null
        else items.sorted()[items.size / 2].formatAsRuntime()
    }

    val seasonInfo = listOfNotNull(
        year, runtime, season.statistics?.sizeOnDisk?.bytesAsFileSizeString()
    )
    val infoString = seasonInfo.joinToString(Bullet)
    Text(
        text = infoString,
        fontSize = 16.sp
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
    ) {
        IconButton (
            onClick = onDeleteSeason,
            shape = RoundedCornerShape(10.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            enabled = !deleteInProgress
        ) {
            if (deleteInProgress) {
                CircularProgressIndicator(Modifier.size(24.dp))
            } else {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }

        ReleaseDownloadButtons(
            onInteractiveClicked = {
                seriesId?.let { seriesId ->
                    val destination = ArrScreen.SeriesRelease(
                        seriesId = seriesId,
                        seasonNumber = season.seasonNumber
                    )
                    navigation.navigateTo(destination)
                }
            },
            onAutomaticClicked = {
                onPerformAutomaticSearch(season.seasonNumber)
            },
            automaticSearchInProgress = searchInProgress(season.seasonNumber),
            modifier = Modifier.weight(1f),
            smallSpacing = true,
            automaticSearchEnabled = episodes.any { it.monitored }
        )
    }
}