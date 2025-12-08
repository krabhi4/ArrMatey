package com.dnfapps.arrmatey.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arrmatey.shared.generated.resources.Res
import arrmatey.shared.generated.resources.season
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.ArrMovie
import com.dnfapps.arrmatey.api.arr.model.ArrSeries
import com.dnfapps.arrmatey.api.arr.model.CoverType
import com.dnfapps.arrmatey.api.arr.model.SeriesStatus
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.extensions.pxToDp
import com.dnfapps.arrmatey.ui.theme.TranslucentBlack
import com.dnfapps.arrmatey.utils.formatNextAiringTime
import com.dnfapps.arrmatey.utils.formatReleaseDate
import com.skydoves.cloudy.cloudy
import org.jetbrains.compose.resources.pluralStringResource

private val defaultHeight = 100.dp

@Composable
fun <T: AnyArrMedia> MediaList(
    items: List<T>,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            MediaItem(item, onItemClick)
        }
    }
}

@Composable
fun <T: AnyArrMedia> MediaItem(
    item: T,
    onItemClick: (T) -> Unit
) {
    var cardHeight by remember { mutableStateOf(0.dp) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(item)
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val banner = item.images.firstOrNull { it.coverType == CoverType.Banner }?.remoteUrl
                ?: item.images.firstOrNull { it.coverType == CoverType.Poster }?.remoteUrl
            banner?.let { bannerUrl ->
                val bannerModel = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(bannerUrl)
                    .diskCacheKey(bannerUrl)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .crossfade(true)
                    .build()
                AsyncImage(
                    model = bannerModel,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .cloudy(radius = 40)
                        .matchParentSize()
                )
            }
            Box(modifier = Modifier.fillMaxWidth().height(cardHeight).background(TranslucentBlack))

            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.padding(12.dp).fillMaxWidth().onGloballyPositioned {
                    cardHeight = it.size.height.pxToDp() + 24.dp
                },
            ) {
                val posterUrl = item.images.firstOrNull { it.coverType == CoverType.Poster }?.remoteUrl
                val model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(posterUrl)
                    .diskCacheKey(posterUrl)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .crossfade(true)
                    .build()
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    modifier = Modifier
                        .height(defaultHeight)
                        .aspectRatio(0.675f, true)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )

                Column(
                    modifier = Modifier.defaultMinSize(minHeight = defaultHeight)
                ) {
                    Text(
                        text = "${item.title} (${item.year})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    MediaDetails(item)
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.MediaDetails(item: AnyArrMedia) {
    when (item) {
        is ArrSeries -> SeriesDetails(item)
        is ArrMovie -> MovieDetails(item)
    }
}

@Composable
private fun ColumnScope.SeriesDetails(item: ArrSeries) {
    val countStr = "${item.episodeFileCount}/${item.episodeCount} (${(item.statusProgress).toInt()}%)"
    val seasonLabel = pluralStringResource(Res.plurals.season, item.seasonCount)
    val seasonCountStr = "${item.seasonCount} $seasonLabel"
    val fileSizeString = item.fileSize.bytesAsFileSizeString()
    val network = item.network

    val firstLine = listOfNotNull(network, seasonCountStr, fileSizeString).joinToString(" • ")
    Text(firstLine, color = Color.White)

    val statusStr = when (item.status) {
        SeriesStatus.Continuing -> item.formatNextAiringTime() ?: "${item.status.name} - Unknown"
        else -> item.status.name
    }
    Text(statusStr, color = Color.White)

    Spacer(modifier = Modifier.weight(1f))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(bottom = 1.dp)
    ) {
        Text(text = item.episodeFileCount.toString(), fontSize = 12.sp, color = Color.White)
        Text(text = "/${item.episodeCount}", fontSize = 12.sp, color = Color.White)
    }
    LinearProgressIndicator(
        progress = { item.statusProgress },
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp),
        color = item.statusColor
    )
}

@Composable
private fun ColumnScope.MovieDetails(item: ArrMovie) {
    item.formatReleaseDate()?.let {
        Text(it, color = Color.White)
    }

    val firstLine = listOfNotNull(item.runtimeString, item.studio).joinToString(" • ")
    Text(firstLine, color = Color.White)

    val statusLabel = item.status.name.takeIf { item.fileSize == 0L }
    val secondLine = listOfNotNull(statusLabel, item.fileSize.bytesAsFileSizeString(), item.movieFile?.quality?.quality?.name).joinToString(" • ")
    Text(secondLine, color = Color.White)

    Spacer(modifier = Modifier.weight(1f))
    LinearProgressIndicator(
        progress = { item.statusProgress },
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp),
        color = item.statusColor
    )
}