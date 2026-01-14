package com.dnfapps.arrmatey.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.api.arr.model.MovieFile
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.entensions.Bullet
import com.dnfapps.arrmatey.utils.format
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun MovieFileCard(file: MovieFile) {
    ContainerCard {
        Text(
            text = file.relativePath,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = listOf(
                file.languages.first().name,
                file.size.bytesAsFileSizeString(),
                file.quality.qualityLabel
            ).joinToString(Bullet),
            fontSize = 14.sp
        )
        val formattedDate = file.dateAdded.format("MMM d, yyyy")
        Text(
            text = stringResource(R.string.added_on, formattedDate),
            fontSize = 14.sp
        )
    }
}