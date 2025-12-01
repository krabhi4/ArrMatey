package com.dnfapps.arrmatey.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.dnfapps.arrmatey.api.arr.model.ArrMedia
import com.dnfapps.arrmatey.api.arr.model.CoverType

@Composable
fun <T: ArrMedia<*,*,*,*,*>> PosterGrid(
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
            val url = item.images.firstOrNull { it.coverType == CoverType.Poster }?.remoteUrl
            val model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(url)
                .diskCacheKey(url)
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
            Card(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .aspectRatio(0.675f, true)
                    .clickable {
                        onItemClick(item)
                    }
            ) {
                Box {
                    AsyncImage(
                        model = model,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp)),
                        contentScale = ContentScale.FillBounds
                    )
                    LinearProgressIndicator(
                        progress = { item.statusProgress },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .height(10.dp),
                        color = item.statusColor
                    )
                }
            }
        }
    }
}