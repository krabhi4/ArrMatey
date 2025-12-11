package com.dnfapps.arrmatey.api.arr.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.ui.theme.RadarrDownloadedMonitored
import com.dnfapps.arrmatey.ui.theme.RadarrDownloadedUnmonitored
import com.dnfapps.arrmatey.ui.theme.RadarrMissingMonitored
import com.dnfapps.arrmatey.ui.theme.RadarrMissingUnmonitored
import com.dnfapps.arrmatey.ui.theme.RadarrUnreleased
import com.dnfapps.arrmatey.utils.format
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
@Entity(
    tableName = "arr_movies",
    foreignKeys = [
        ForeignKey(
            entity = Instance::class,
            parentColumns = ["id"],
            childColumns = ["instanceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("instanceId")]
)
data class ArrMovie(
    @PrimaryKey(autoGenerate = false)
    override val id: Int,
    override val title: String,
    override val originalLanguage: Language,
    override val year: Int,
    override val qualityProfileId: Int,
    override val monitored: Boolean,
    override val runtime: Int,
    override val tmdbId: Int,
    override val status: MovieStatus,
    override val sortTitle: String? = null,
    override val overview: String? = null,
    override val path: String? = null,
    override val cleanTitle: String? = null,
    override val imdbId: String? = null,
    override val titleSlug: String? = null,
    override val rootFolderPath: String? = null,
    override val folder: String? = null,
    override val certification: String? = null,
    override val images: List<ArrImage> = emptyList(),
    override val alternateTitles: List<MovieAlternateTitle> = emptyList(),
    override val genres: List<String> = emptyList(),
    override val tags: List<Int> = emptyList(),
    override val addOptions: MovieAddOptions? = null,
    override val ratings: MovieRatings,
    override val statistics: MovieStatistics,
    @Contextual override val added: Instant,
    override var instanceId: Long? = null,

    val originalTitle: String,
    val secondaryYear: Int? = null,
    val secondaryYearSourceId: Int,
    val sizeOnDisk: Long,
    @Contextual val inCinemas: Instant? = null,
    @Contextual val physicalRelease: Instant? = null,
    @Contextual val digitalRelease: Instant? = null,
    @Contextual val releaseDate: Instant? = null,
    val physicalReleaseNote: String? = null,
    val website: String,
    val remotePoster: String? = null,
    val youTubeTrailerId: String,
    val studio: String,
    val hasFile: Boolean,
    val movieFileId: Int,
    val minimumAvailability: MovieStatus,
    val isAvailable: Boolean,
    val folderName: String,
    val keywords: List<String> = emptyList(),
    val movieFile: MovieFile? = null,
    val collection: MovieCollection? = null,
    val popularity: Double,
    val lastSearchTime: String? = null,
): ArrMedia<MovieAlternateTitle, MovieAddOptions, MovieRatings, MovieStatistics, MovieStatus>() {

    override fun ratingScore(): Double {
        val imdb = ratings.imdb?.value
        val rt = ratings.rottenTomatoes?.value?.apply { this/10 }
        val tmdb = ratings.tmdb?.value
        val mtc = ratings.metacritic?.value?.apply { this/10 }
        val trakt = ratings.trakt?.value

        val avail = listOfNotNull(imdb, rt, tmdb, mtc, trakt)
        return avail.sum() / avail.size
    }

    override val statusString: String
        get() = status.name

    override val statusProgress: Float
        get() = 1f

    override val statusColor: Color
        get() = when {
            status == MovieStatus.Tba || status == MovieStatus.Announced -> RadarrUnreleased
            movieFile != null && monitored -> RadarrDownloadedMonitored
            movieFile != null && !monitored -> RadarrDownloadedUnmonitored
            movieFile == null && monitored -> RadarrMissingMonitored
            movieFile == null && !monitored -> RadarrMissingUnmonitored
            else -> Color.Unspecified
        }

    override val releasedBy: String?
        get() = studio

    val ratingsAsMap: Map<RatingType, MovieRating?>
        get() = mapOf(
            RatingType.Tmdb to ratings.tmdb,
            RatingType.Imdb to ratings.imdb,
            RatingType.Metacritic to ratings.metacritic,
            RatingType.RottenTomatoes to ratings.rottenTomatoes,
            RatingType.Trakt to ratings.trakt
        )

    override fun setMonitored(monitored: Boolean): ArrMovie {
        return copy(monitored = monitored)
    }

    override val infoItems: List<Info>
        get() = listOfNotNull(
            Info(
                label = "Minimum Availability",
                value = minimumAvailability.name
            ),
            Info(
                label = "Root Folder",
                value = rootFolderPath ?: "Unknown"
            ),
            inCinemas?.format()?.let {
                Info(
                    label = "In Cinemas",
                    value = it
                )
            },
            digitalRelease?.format()?.let {
                Info(
                    label = "Digital Release",
                    value = it
                )
            },
            physicalRelease?.format()?.let {
                Info(
                    label = "Physical Release",
                    value = it
                )
            }
        )

}