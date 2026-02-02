package com.dnfapps.arrmatey.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arrmatey.composeapp.generated.resources.Res
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.api.model.ArrMovie
import com.dnfapps.arrmatey.arr.api.model.ArrSeries
import com.dnfapps.arrmatey.arr.api.model.QualityProfile
import com.dnfapps.arrmatey.arr.api.model.SeriesMonitorNewItems
import com.dnfapps.arrmatey.arr.api.model.Tag
import com.dnfapps.arrmatey.entensions.forEachIndexed
import com.dnfapps.arrmatey.entensions.getString
import com.dnfapps.arrmatey.utils.format
import kotlin.time.ExperimentalTime

@Composable
fun InfoArea(
    item: ArrMedia,
    qualityProfiles: List<QualityProfile>,
    tags: List<Tag>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.information),
            fontWeight = FontWeight.Medium,
            fontSize = 26.sp
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column (
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                val infoItems = when (item) {
                    is ArrSeries -> seriesInfo(item, qualityProfiles, tags)
                    is ArrMovie -> movieInfo(item, qualityProfiles, tags)
                }
                infoItems.forEachIndexed { index, (key, value) ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = key, fontSize = 14.sp)
                        Text(
                            text = value,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            fontSize = 14.sp,
                            modifier = Modifier.widthIn(max = 200.dp)
                        )
                    }
                    if (index < infoItems.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun seriesInfo(
    series: ArrSeries,
    qualityProfiles: List<QualityProfile>,
    tags: List<Tag>
): Map<String, String> {
    val qualityProfile = qualityProfiles.firstOrNull { it.id == series.qualityProfileId }
    val tagsLabel = series.formatTags(tags) ?: stringResource(R.string.none)

    val unknown = stringResource(R.string.unknown)
    val monitorLabel = if (series.monitorNewItems == SeriesMonitorNewItems.All) {
        stringResource(R.string.monitored)
    } else { stringResource(R.string.unmonitored) }

    val seasonFolderLabel = if (series.seasonFolder) {
        stringResource(R.string.yes)
    } else { stringResource(R.string.no) }

    return mapOf(
        stringResource(R.string.series_type) to series.seriesType.name,
        stringResource(R.string.root_folder) to (series.rootFolderPath ?: unknown),
        stringResource(R.string.path) to (series.path ?: unknown),
        stringResource(R.string.new_seasons) to monitorLabel,
        stringResource(R.string.season_folders) to seasonFolderLabel,
        stringResource(R.string.quality_profile) to (qualityProfile?.name ?: unknown),
        stringResource(R.string.tags) to tagsLabel
    )
}

@OptIn(ExperimentalTime::class)
@Composable
private fun movieInfo(
    movie: ArrMovie,
    qualityProfiles: List<QualityProfile>,
    tags: List<Tag>
): Map<String, String> {
    val qualityProfile = qualityProfiles.firstOrNull { it.id == movie.qualityProfileId }
    val tagsLabel = movie.formatTags(tags) ?: stringResource(R.string.none)

    val unknown = stringResource(R.string.unknown)

    val rootFolderPathValue = movie.rootFolderPath.takeUnless { it.isBlank() }
        ?: stringResource(R.string.unknown)

    return buildMap {
        put(stringResource(R.string.minimum_availability), movie.minimumAvailability.name)
        put(stringResource(R.string.root_folder), rootFolderPathValue)
        put(stringResource(R.string.path), (movie.path ?: unknown))
        movie.inCinemas?.format("MMM d, yyyy")?.let {
            put(stringResource(R.string.in_cinemas), it)
        }
        movie.physicalRelease?.format("MMM d, yyyy")?.let {
            put(stringResource(R.string.physical_release), it)
        }
        movie.digitalRelease?.format("MMM d, yyyy")?.let {
            put(stringResource(R.string.digital_release), it)
        }
        put(stringResource(R.string.quality_profile), (qualityProfile?.name ?: unknown))
        put(stringResource(R.string.tags), tagsLabel)
    }

}