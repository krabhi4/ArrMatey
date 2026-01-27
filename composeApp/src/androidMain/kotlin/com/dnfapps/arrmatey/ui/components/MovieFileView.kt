package com.dnfapps.arrmatey.ui.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.arr.api.model.ArrMovie
import com.dnfapps.arrmatey.arr.api.model.ExtraFile
import com.dnfapps.arrmatey.navigation.ArrScreen
import com.dnfapps.arrmatey.navigation.Navigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun MovieFileView(
    movie: ArrMovie,
    movieExtraFiles: List<ExtraFile>,
    searchIds: Set<Long>,
    onAutomaticSearch: (Long) -> Unit,
    navigationManager: NavigationManager = koinInject(),
    navigation: Navigation<ArrScreen> = navigationManager.movies()
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReleaseDownloadButtons(
            onInteractiveClicked = {
                val destination = ArrScreen.MovieReleases(movie.id!!)
                navigation.navigateTo(destination)
            },
            onAutomaticClicked = {
                movie.id?.let { id ->
                    onAutomaticSearch(id)
                }
            },
            automaticSearchEnabled = movie.monitored,
            automaticSearchInProgress = searchIds.contains(movie.id),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

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
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    navigation.navigateTo(ArrScreen.MovieFiles(movie))
                }
            )
        }
        movie.movieFile?.let { file ->
            FileCard(file)
        }
        movieExtraFiles.takeUnless { it.isEmpty() }?.forEach { extraFile ->
            ExtraFileCard(extraFile)
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