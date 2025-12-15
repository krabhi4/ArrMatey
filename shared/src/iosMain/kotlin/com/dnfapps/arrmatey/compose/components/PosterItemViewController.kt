package com.dnfapps.arrmatey.compose.components

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia

fun <T: AnyArrMedia> PosterItemViewController(
    item: T,
    onItemClick: ((T) -> Unit)? = null,
    enabled: Boolean = true
) = ComposeUIViewController {
    PosterItem(
        item = item,
        onItemClick = onItemClick,
        enabled = enabled,
        elevation = 0.dp
    )
}