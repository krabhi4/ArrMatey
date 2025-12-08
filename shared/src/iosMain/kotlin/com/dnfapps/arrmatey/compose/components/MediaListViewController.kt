package com.dnfapps.arrmatey.compose.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia

fun MediaListViewController(
    items: List<AnyArrMedia>,
    onItemClick: (AnyArrMedia) -> Unit = {}
) = ComposeUIViewController {
    MediaList(
        items = items,
        onItemClick = onItemClick,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxSize()
    )
}