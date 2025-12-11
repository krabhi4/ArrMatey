package com.dnfapps.arrmatey.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.model.ArrMedia
import com.dnfapps.arrmatey.api.arr.model.CoverType

@Composable
fun <T: AnyArrMedia> PosterGrid(
    items: List<T>,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(items) { item ->
            PosterItem(
                item = item,
                onItemClick = onItemClick,
                modifier = Modifier.padding(8.dp),
                additionalContent = {
                    LinearProgressIndicator(
                        progress = { item.statusProgress },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .height(6.dp),
                        color = item.statusColor
                    )
                }
            )
        }
    }
}

@Composable
fun <T: AnyArrMedia> PosterItem(
    item: T,
    modifier: Modifier = Modifier,
    onItemClick: ((T) -> Unit)? = null,
    enabled: Boolean = true,
    additionalContent: @Composable BoxScope.() -> Unit = {}
) {
    var imageLoadError by remember { mutableStateOf(false) }
    var imageLoaded by remember { mutableStateOf(false) }

    val url = item.images.firstOrNull { it.coverType == CoverType.Poster }?.remoteUrl
    val model = ImageRequest.Builder(LocalPlatformContext.current)
        .data(url)
        .diskCacheKey(url)
        .networkCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .crossfade(true)
        .listener(
            onError = { _, err ->
                println(err.throwable.message)
                imageLoadError = true
            },
            onSuccess = { _, _ -> imageLoaded = true }
        )
        .build()
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .aspectRatio(0.675f, true)
            .clickable(
                enabled = enabled && onItemClick != null,
                onClick = {
                    onItemClick?.invoke(item)
                }
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp)),
                contentScale = ContentScale.FillBounds
            )
            if (imageLoadError) {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp).align(Alignment.Center)
                )
            }
            additionalContent()

        }
    }
}