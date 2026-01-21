package com.dnfapps.arrmatey.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ExpandCircleDown
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.arr.api.model.ArrSeries
import com.dnfapps.arrmatey.arr.api.model.Episode
import com.dnfapps.arrmatey.arr.api.model.SonarrQueueItem
import com.dnfapps.arrmatey.arr.viewmodel.ActivityQueueViewModel
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.Navigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SeasonsArea(
    series: ArrSeries,
    episodes: List<Episode>,
    searchIds: Set<Long>,
    searchResult: Boolean?,
    onToggleSeasonMonitor: (Int) -> Unit,
    onToggleEpisodeMonitor: (Episode) -> Unit,
    onEpisodeAutomaticSearch: (Long) -> Unit,
    onSeasonAutomaticSearch: (Int) -> Unit,
    activityQueueViewModel: ActivityQueueViewModel = koinInject(),
    navigationManager: NavigationManager = koinInject(),
    navigation: Navigation<ArrScreen> = navigationManager.series()
) {

    val queueItems by activityQueueViewModel.activityTasks.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val searchQueuedMessage = stringResource(R.string.search_queued)
    val searchErrorMessage = stringResource(R.string.search_error)

    LaunchedEffect(searchResult) {
        when (searchResult) {
            true -> Toast.makeText(context, searchQueuedMessage, Toast.LENGTH_SHORT).show()
            false -> Toast.makeText(context, searchErrorMessage, Toast.LENGTH_SHORT).show()
            else -> {}
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.seasons),
            fontWeight = FontWeight.Medium,
            fontSize = 26.sp
        )
        series.seasons.sortedByDescending { it.seasonNumber }.forEach { season ->
            var expanded by rememberSaveable { mutableStateOf(false) }
            val iconRotation by animateFloatAsState(
                targetValue = if (expanded) 180f else 0f,
                animationSpec = tween(durationMillis = 200),
                label = "iconRotation"
            )
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                ContainerCard (
                    modifier = Modifier.clickable { expanded = !expanded }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (season.seasonNumber == 0) {
                                stringResource(R.string.specials)
                            } else {
                                "${stringResource(R.string.season_singular)} ${season.seasonNumber}"
                            },
                            fontWeight = FontWeight.Medium,
                            fontSize = 22.sp
                        )
                        season.statistics?.let { statistics ->
                            Text(
                                text = "${statistics.episodeFileCount}/${statistics.totalEpisodeCount}",
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.ExpandCircleDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(iconRotation)
                        )
                        Icon(
                            imageVector = if (season.monitored) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (season.monitored) {
                                stringResource(R.string.monitored)
                            } else {
                                stringResource(R.string.unmonitored)
                            },
                            modifier = Modifier.clickable {
                                onToggleSeasonMonitor(season.seasonNumber)
                            }
                        )
                    }
                }
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        val seasonEpisodes = episodes
                            .filter { it.seasonNumber == season.seasonNumber }
                            .sortedByDescending { it.episodeNumber }

                        Spacer(modifier = Modifier.height(6.dp))
                        SeasonHeader(
                            seriesId = series.id,
                            season = season,
                            episodes = seasonEpisodes,
                            onPerformAutomaticSearch = onSeasonAutomaticSearch,
                            searchInProgress = { searchIds.contains(it.toLong()) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        seasonEpisodes.forEachIndexed { index, episode ->
                            val isActive by remember { derivedStateOf {
                                queueItems.any { item ->
                                    when(item) {
                                        is SonarrQueueItem -> item.calcEpisodeId == episode.id ||
                                                (item.calcSeriesId == episode.seriesId && item.seasonNumber == episode.seasonNumber)
                                        else -> false
                                    }
                                }
                            } }
                            val activityProgress by remember { derivedStateOf {
                                queueItems.firstOrNull { it is SonarrQueueItem && it.episodeId == episode.id }?.progressLabel
                                    ?: queueItems.firstOrNull { it is SonarrQueueItem && it.calcSeriesId == episode.seriesId && it.seasonNumber == episode.seasonNumber }?.progressLabel
                            } }
                            EpisodeRow(
                                episode = episode,
                                isActive = isActive,
                                progressLabel = activityProgress,
                                onClick = {
                                    val destination = ArrScreen.EpisodeDetails(series, episode)
                                    navigation.navigateTo(destination)
                                },
                                onAutomaticSearch = onEpisodeAutomaticSearch,
                                onToggleMonitor = onToggleEpisodeMonitor,
                                searchInProgress = { searchIds.contains(it) }
                            )
                            if (index < seasonEpisodes.size-1) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}