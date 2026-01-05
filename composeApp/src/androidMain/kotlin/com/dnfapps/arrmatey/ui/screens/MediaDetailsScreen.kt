package com.dnfapps.arrmatey.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandCircleDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.api.arr.model.Episode
import com.dnfapps.arrmatey.api.arr.model.Season
import com.dnfapps.arrmatey.api.arr.model.SeriesStatus
import com.dnfapps.arrmatey.api.arr.viewmodel.DetailsUiState
import com.dnfapps.arrmatey.api.arr.viewmodel.EpisodeUiState
import com.dnfapps.arrmatey.compose.components.DetailHeaderBanner
import com.dnfapps.arrmatey.compose.components.PosterItem
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.extensions.formatAsRuntime
import com.dnfapps.arrmatey.extensions.isToday
import com.dnfapps.arrmatey.extensions.isTodayOrAfter
import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrTabNavigation
import com.dnfapps.arrmatey.ui.components.OverlayTopAppBar
import com.dnfapps.arrmatey.ui.helpers.statusBarHeight
import com.dnfapps.arrmatey.ui.tabs.LocalArrViewModel
import com.dnfapps.arrmatey.ui.viewmodel.ArrViewModel
import com.dnfapps.arrmatey.ui.viewmodel.RadarrViewModel
import com.dnfapps.arrmatey.ui.viewmodel.SonarrViewModel
import com.dnfapps.arrmatey.utils.format
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaDetailsScreen(
    type: InstanceType,
    id: Int,
    navigation: ArrTabNavigation = koinInject<ArrTabNavigation>(parameters = { parametersOf(type) })
) {
    val arrViewModel = LocalArrViewModel.current

    var isMonitored by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues.copy(bottom = 0.dp, top = 0.dp))
                .fillMaxSize()
        ) {
            arrViewModel?.let { arrViewModel ->
                val detailUiState by arrViewModel.detailsUiState.collectAsState()
                var isRefreshing by remember { mutableStateOf(true) }

                LaunchedEffect(isRefreshing) {
                    arrViewModel.getDetails(id)
                    isRefreshing = false
                }

                when (val state = detailUiState) {
                    is DetailsUiState.Initial,
                    is DetailsUiState.Loading -> {
                        LoadingIndicator(
                            modifier = Modifier
                                .size(96.dp)
                                .align(Alignment.Center)
                        )
                    }
                    is DetailsUiState.Error -> {
                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = { isRefreshing = true }
                        ) {
                            Text(text = state.error.message)
                        }
                    }
                    is DetailsUiState.Success -> {
                        val item = state.item

                        SideEffect {
                            isMonitored = item.monitored
                        }

                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            onRefresh = { isRefreshing = true }
                        ) {
                            Column(
                                modifier = Modifier.verticalScroll(scrollState),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DetailsHeader(item)

                                Column(
                                    modifier = Modifier.padding(horizontal = 24.dp),
                                    verticalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    UpcomingDateView(item)

                                    ItemDescriptionCard(item)

                                    FilesArea(item, arrViewModel)

                                    InfoArea(item)
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }

            OverlayTopAppBar(
                scrollState = scrollState,
                modifier = Modifier.align(Alignment.TopCenter),
                navigationIcon = {
                    IconButton(
                        onClick = { navigation.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            arrViewModel?.setMonitorStatus(id, !isMonitored)
                        }
                    ) {
                        Icon(
                            imageVector = if (isMonitored) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun DetailsHeader(item: AnyArrMedia) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        DetailHeaderBanner(item)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 170.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PosterItem(
                item = item,
                modifier = Modifier.height(220.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 42.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = listOf(
                        item.year,
                        item.runtimeString,
                        item.certification
                    ).joinToString(" • "),
                    fontSize = 16.sp
                )
                Text(
                    text = listOf(item.releasedBy, item.statusString).joinToString(" • "),
                    fontSize = 14.sp,
                    lineHeight = 16.sp
                )
                Text(
                    text = item.genres.joinToString(" • "),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun ItemDescriptionCard(item: AnyArrMedia) {
    item.overview?.let { overview ->
        var expanded by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable(enabled = !expanded) {
                    expanded = true
                },
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 18.dp)
            ) {
                Text(
                    text = overview,
                    maxLines = if (expanded) Int.MAX_VALUE else 10,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    onTextLayout = { result ->
                        if (!result.didOverflowHeight) {
                            expanded = true
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun UpcomingDateView(item: AnyArrMedia) {
    when (item) {
        is ArrSeries -> if (item.status == SeriesStatus.Continuing) item.nextAiring?.format()?.let {
                "${stringResource(R.string.airing_next)} $it"
            } ?: stringResource(R.string.continuing_unknown) else null
        is ArrMovie -> item.inCinemas?.format()?.takeUnless {
            item.digitalRelease != null || item.physicalRelease != null
        }?.let { "${stringResource(R.string.in_cinemas)} $it" }
    }?.let { airingString ->
        Text(
            text = airingString,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun FilesArea(item: AnyArrMedia, vm: ArrViewModel) {
    when (item) {
        is ArrSeries -> SeasonsArea(item, vm as SonarrViewModel)
        is ArrMovie -> MovieFileView(item)
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun MovieFileView(movie: ArrMovie) {
    val arrViewModel = LocalArrViewModel.current
    if (arrViewModel == null || arrViewModel !is RadarrViewModel) return

    val context = LocalContext.current

    val searchIds by arrViewModel.automaticSearchIds.collectAsStateWithLifecycle()
    val searchResult by arrViewModel.automaticSearchResult.collectAsStateWithLifecycle()

    val movieExtraFileMap by arrViewModel.movieExtraFilesMap.collectAsStateWithLifecycle()
    val movieExtraFiles = remember(movieExtraFileMap) {
        movieExtraFileMap[movie.id] ?: emptyList()
    }

    LaunchedEffect(Unit) {
        movie.id?.let {
            arrViewModel.getMovieExtraFile(it)
        }
    }

    LaunchedEffect(searchResult) {
        when (searchResult) {
            true -> Toast.makeText(context, "Search queued", Toast.LENGTH_SHORT).show()
            false -> Toast.makeText(context, "Search error", Toast.LENGTH_SHORT).show()
            else -> {}
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .padding(horizontal = 24.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Interactive"
                )
                Text(text = "Interactive")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    movie.id?.let { id ->
                        arrViewModel.performSearch(listOf(id))
                    }
                },
                enabled = !searchIds.contains(movie.id)
            ) {
                if (searchIds.contains(movie.id)) {
                    CircularProgressIndicator(modifier = Modifier.size(25.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Automatic"
                    )
                    Text(text = "Automatic")
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.files),
                fontWeight = FontWeight.Medium,
                fontSize = 26.sp
            )
            Text(
                text = stringResource(R.string.history),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable {

                }
            )
        }
        movie.movieFile?.let { file ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 18.dp)
                ) {
                    Text(
                        text = file.relativePath,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = listOf(
                            file.languages.first().name,
                            file.size.bytesAsFileSizeString(),
                            file.quality.quality.name
                        ).joinToString(" • "),
                        fontSize = 14.sp
                    )
                    Text(
                        text = stringResource(R.string.added_on, file.dateAdded.format("MMM d, yyyy")),
                        fontSize = 14.sp
                    )
                }
            }
        }
        movieExtraFiles.takeUnless { it.isEmpty() }?.forEach { extraFile ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 18.dp)
                ) {
                    Text(
                        text = extraFile.relativePath,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = extraFile.type.name,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (movie.movieFile == null && movieExtraFiles.isEmpty()) {
            Text(
                text = stringResource(R.string.no_files),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SeasonsArea(series: ArrSeries, vm: SonarrViewModel) {
    val episodeState by vm.episodeState.collectAsStateWithLifecycle()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
            Card(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { expanded = !expanded }
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
                                vm.toggleSeasonMonitor(series, season.seasonNumber)
                            }
                        )
                    }
                    if (expanded) {
                        when (val state = episodeState) {
                            is EpisodeUiState.Initial -> {}
                            is EpisodeUiState.Loading -> {
                                Spacer(modifier = Modifier.weight(1f))
                                LoadingIndicator(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(96.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            is EpisodeUiState.Success -> {
                                val seasonEpisodes = state.items
                                        .filter { it.seasonNumber == season.seasonNumber }
                                        .sortedByDescending { it.episodeNumber }

                                Spacer(modifier = Modifier.height(6.dp))
                                SeasonHeader(season, seasonEpisodes)
                                Spacer(modifier = Modifier.height(12.dp))
                                seasonEpisodes.forEachIndexed { index, episode ->
                                    EpisodeRow(episode, vm)
                                    if (index < seasonEpisodes.size-1) {
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    }
                                }
                            }
                            is EpisodeUiState.Error -> {
                                Text("ERROR")
                                Text(state.error.message)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun SeasonHeader(season: Season, episodes: List<Episode>) {
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
    val infoString = seasonInfo.joinToString(" • ")
    Text(
        text = infoString,
        fontSize = 16.sp
    )
    if (false) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }

            Button(
                onClick = {},
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun EpisodeRow(episode: Episode, vm: SonarrViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            val titleString = buildAnnotatedString {
                withStyle(SpanStyle(fontSize = 16.sp)) {
                    withStyle((SpanStyle(color = MaterialTheme.colorScheme.primary))) {
                        append("${episode.episodeNumber}. ")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(episode.title ?: "")
                    }
                    episode.finaleType?.let { finalType ->
                        withStyle(SpanStyle(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )) {
                            append(" • ")
                            append(finalType.label)
                        }
                    }
                }
            }
            Text(
                text = titleString,
                lineHeight = 16.sp
            )

            val statusString =
                episode.episodeFile?.qualityName
                    ?: episode.airDate?.takeIf { it.isTodayOrAfter() }?.let {
                        stringResource(R.string.unaired)
                    }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                statusString?.let {
                    Text(
                        text = statusString,
                        fontSize = 14.sp
                    )
                } ?:
                    Text(
                        text = stringResource(R.string.missing),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error
                    )

                val (weight, color) = if (episode.airDate?.isToday() == true)
                    FontWeight.Medium to MaterialTheme.colorScheme.primary
                else
                    FontWeight.Normal to Color.Unspecified
                Text(
                    text = " • ${episode.formatAirDateUtc()}",
                    color = color,
                    fontWeight = weight,
                    fontSize = 14.sp
                )
            }
        }
        if (false) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.clickable {

                }
            )
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.clickable {

                }
            )
        }
        Icon(
            imageVector = if (episode.monitored) {
                Icons.Default.Bookmark
            } else {
                Icons.Default.BookmarkBorder
            },
            contentDescription = null,
            modifier = Modifier.clickable {
                vm.toggleEpisodeMonitor(episode.id)
            }
        )
    }
}

@Composable
private fun InfoArea(item: AnyArrMedia) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.information),
            fontWeight = FontWeight.Medium,
            fontSize = 26.sp
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column (
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                val infoItems by item.infoItems.collectAsStateWithLifecycle(emptyList())
                if (infoItems.isEmpty()) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                infoItems.forEachIndexed { index, info ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = info.label, fontSize = 14.sp)
                        Text(
                            text = info.value,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            fontSize = 14.sp
                        )
                    }
                    if (index < infoItems.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}