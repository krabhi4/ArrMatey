package com.dnfapps.arrmatey.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.ui.theme.SonarrDownloading
import com.dnfapps.arrmatey.utils.AspectRatio

@Composable
fun PosterGrid(
    aspectRatio: AspectRatio,
    items: List<ArrMedia>,
    onItemClick: (ArrMedia) -> Unit,
    itemIsActive: (ArrMedia) -> Boolean,
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        userScrollEnabled = userScrollEnabled
    ) {
        items(items) { item ->
            val isActive = itemIsActive(item)
            PosterItem(
                aspectRatio = aspectRatio,
                item = item,
                onItemClick = onItemClick,
                modifier = Modifier.padding(8.dp),
                additionalContent = {
                    if (item.id != null) {
                        LinearProgressIndicator(
                            progress = { item.statusProgress },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .height(6.dp),
                            color = if (isActive) SonarrDownloading else item.statusColor
                        )
                    }
                }
            )
        }
    }
}