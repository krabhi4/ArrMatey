package com.dnfapps.arrmatey.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dnfapps.arrmatey.R

@Composable
fun ReleaseDownloadButtons(
    onInteractiveClicked: () -> Unit,
    automaticSearchEnabled: Boolean,
    onAutomaticClicked: () -> Unit,
    automaticSearchInProgress: Boolean,
    modifier: Modifier = Modifier,
    smallSpacing: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(if (smallSpacing) 12.dp else 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = onInteractiveClicked,
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.interactive)
            )
            Text(text = stringResource(R.string.interactive))
        }

        Button(
            modifier = Modifier.weight(1f),
            onClick = onAutomaticClicked,
            enabled = automaticSearchEnabled && !automaticSearchInProgress,
            shape = RoundedCornerShape(10.dp)
        ) {
            if (automaticSearchInProgress) {
                CircularProgressIndicator(modifier = Modifier.size(25.dp))
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.automatic)
                )
                Text(text = stringResource(R.string.automatic))
            }
        }
    }
}