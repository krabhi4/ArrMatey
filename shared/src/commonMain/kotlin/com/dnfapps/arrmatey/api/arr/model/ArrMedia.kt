package com.dnfapps.arrmatey.api.arr.model

import androidx.compose.ui.graphics.Color
import androidx.room.Ignore
import com.dnfapps.arrmatey.extensions.formatAsRuntime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Instant

interface AnyArrMedia {
    val id: Int
    val title: String
    val originalLanguage: Language
    val year: Int
    val qualityProfileId: Int
    val monitored: Boolean
    val runtime: Int
    val tmdbId: Int
    val images: List<ArrImage>
    val sortTitle: String?
    val overview: String?
    val path: String?
    val cleanTitle: String?
    val imdbId: String?
    val titleSlug: String?
    val rootFolderPath: String?
    val folder: String?
    val certification: String?
    val genres: List<String>
    val tags: List<Int>
    val statistics: ArrStatistics
    @Contextual val added: Instant
    fun ratingScore(): Double
    val statusProgress: Float
    val statusColor: Color
    val releasedBy: String?
    val statusString: String
    val fileSize: Long
    val runtimeString: String
    val infoItems: Flow<List<Info>>
}

@Serializable
class Info(val label: String, val value: String)

@Serializable
sealed class ArrMedia<AT, AO, R, STAT: ArrStatistics, S>: AnyArrMedia {
    abstract override val id: Int
    abstract override val title: String
    abstract override val originalLanguage: Language
    abstract override val year: Int
    abstract override val qualityProfileId: Int
    abstract override val monitored: Boolean
    abstract override val runtime: Int
    abstract override val tmdbId: Int
    abstract val status: S
    abstract override val images: List<ArrImage>
    abstract override val sortTitle: String?
    abstract override val overview: String?
    abstract override val path: String?
    abstract override val cleanTitle: String?
    abstract override val imdbId: String?
    abstract override val titleSlug: String?
    abstract override val rootFolderPath: String?
    abstract override val folder: String?
    abstract override val certification: String?
    abstract override val genres: List<String>
    abstract override val tags: List<Int>
    abstract val alternateTitles: List<AT>
    abstract val addOptions: AO?
    abstract val ratings: R
    abstract override val statistics: STAT
    @Contextual
    abstract override val added: Instant

    abstract override fun ratingScore(): Double

    abstract override val statusProgress: Float
    abstract override val statusColor: Color
    abstract override val releasedBy: String?
    abstract override val statusString: String

    override val fileSize: Long
        get() = statistics.sizeOnDisk

    override val runtimeString: String
        get() = runtime.formatAsRuntime()

    abstract fun setMonitored(monitored: Boolean): ArrMedia<AT, AO, R, STAT, S>

    @Transient
    protected val _infoItems = MutableStateFlow<List<Info>>(emptyList())
    abstract override val infoItems: Flow<List<Info>>
}