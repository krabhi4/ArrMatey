package com.dnfapps.arrmatey.compose.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.dnfapps.arrmatey.api.arr.model.ArrMedia

fun PosterGridViewController(
    items: List<ArrMedia<*,*,*,*,*>>,
    onItemClick: (ArrMedia<*,*,*,*,*>) -> Unit = {}
) = ComposeUIViewController {
    PosterGrid(
        items = items,
        onItemClick = onItemClick,
        modifier = Modifier.fillMaxSize()
    )
}