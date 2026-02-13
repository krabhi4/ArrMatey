package com.dnfapps.arrmatey.ui.components

import com.dnfapps.arrmatey.shared.MR
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.api.model.ArrMovie
import com.dnfapps.arrmatey.arr.api.model.ArrSeries
import com.dnfapps.arrmatey.arr.api.model.Arrtist
import com.dnfapps.arrmatey.arr.api.model.ArtistMonitorType
import com.dnfapps.arrmatey.arr.api.model.QualityProfile
import com.dnfapps.arrmatey.arr.api.model.MonitorNewItems
import com.dnfapps.arrmatey.arr.api.model.Tag
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.entensions.forEachIndexed
import com.dnfapps.arrmatey.utils.format
import com.dnfapps.arrmatey.utils.mokoString
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
            text = mokoString(MR.strings.information),
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
                    is Arrtist -> artistInfo(item, qualityProfiles, tags)
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
    val tagsLabel = series.formatTags(tags) ?: mokoString(MR.strings.none)

    val unknown = mokoString(MR.strings.unknown)
    val monitorLabel = if (series.monitorNewItems == MonitorNewItems.All) {
        mokoString(MR.strings.monitored)
    } else { mokoString(MR.strings.unmonitored) }

    val seasonFolderLabel = if (series.seasonFolder) {
        mokoString(MR.strings.yes)
    } else { mokoString(MR.strings.no) }

    val diskSize = series.fileSize.bytesAsFileSizeString()

    return mapOf(
        mokoString(MR.strings.series_type) to series.seriesType.name,
        mokoString(MR.strings.size_on_disk) to diskSize,
        mokoString(MR.strings.root_folder) to (series.rootFolderPath ?: unknown),
        mokoString(MR.strings.path) to (series.path ?: unknown),
        mokoString(MR.strings.new_seasons) to monitorLabel,
        mokoString(MR.strings.season_folders) to seasonFolderLabel,
        mokoString(MR.strings.quality_profile) to (qualityProfile?.name ?: unknown),
        mokoString(MR.strings.tags) to tagsLabel
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
    val tagsLabel = movie.formatTags(tags) ?: mokoString(MR.strings.none)

    val unknown = mokoString(MR.strings.unknown)

    val rootFolderPathValue = movie.rootFolderPath.takeUnless { it.isBlank() }
        ?: mokoString(MR.strings.unknown)

    return buildMap {
        put(mokoString(MR.strings.minimum_availability), movie.minimumAvailability.name)
        put(mokoString(MR.strings.root_folder), rootFolderPathValue)
        put(mokoString(MR.strings.path), (movie.path ?: unknown))
        movie.inCinemas?.format("MMM d, yyyy")?.let {
            put(mokoString(MR.strings.in_cinemas), it)
        }
        movie.physicalRelease?.format("MMM d, yyyy")?.let {
            put(mokoString(MR.strings.physical_release), it)
        }
        movie.digitalRelease?.format("MMM d, yyyy")?.let {
            put(mokoString(MR.strings.digital_release), it)
        }
        put(mokoString(MR.strings.quality_profile), (qualityProfile?.name ?: unknown))
        put(mokoString(MR.strings.tags), tagsLabel)
    }

}

@Composable
private fun artistInfo(
    artist: Arrtist,
    qualityProfiles: List<QualityProfile>,
    tags: List<Tag>
): Map<String, String> {
    val qualityProfile = qualityProfiles.firstOrNull { it.id == artist.qualityProfileId }
    val tagsLabel = artist.formatTags(tags) ?: mokoString(MR.strings.none)

    val unknown = mokoString(MR.strings.unknown)
    val monitorLabel = if (artist.monitorNewItems == ArtistMonitorType.All) {
        mokoString(MR.strings.monitored)
    } else { mokoString(MR.strings.unmonitored) }

    val rootFolderPathValue = artist.rootFolderPath?.takeUnless { it.isBlank() }
        ?: mokoString(MR.strings.unknown)

    val diskSize = artist.fileSize.bytesAsFileSizeString()

    return buildMap {
        put(mokoString(MR.strings.size_on_disk), diskSize)
        put(mokoString(MR.strings.root_folder), rootFolderPathValue)
        put(mokoString(MR.strings.path), (artist.path ?: unknown))
        put(mokoString(MR.strings.new_albums), monitorLabel)
        put(mokoString(MR.strings.quality_profile), (qualityProfile?.name ?: unknown))
        put(mokoString(MR.strings.tags), tagsLabel)
    }
}