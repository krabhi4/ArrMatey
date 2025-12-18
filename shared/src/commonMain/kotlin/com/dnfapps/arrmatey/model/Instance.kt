package com.dnfapps.arrmatey.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "instances",
    indices = [
        Index(value = ["url"], unique = true),
        Index(value = ["label"], unique = true)
    ]
)
data class Instance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: InstanceType,
    var label: String,
    var url: String,
    var apiKey: String,
    var enabled: Boolean = true,
    var slowInstance: Boolean = false,
    var customTimeout: Long? = null,
    var selected: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return  false
        other as Instance
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

enum class InstanceType(
    val descriptionKey: String,
    val iconKey: String,
    val github: String,
    val website: String,
    val defaultPort: Int
) {
    Sonarr(
        descriptionKey = "sonarr_description",
        github = "https://github.com/Sonarr/Sonarr",
        website = "https://sonarr.tv/",
        iconKey = "sonarr",
        defaultPort = 8989
    ),
    Radarr(
        descriptionKey = "radarr_description",
        github = "https://github.com/Radarr/Radarr",
        website = "https://radarr.video/",
        iconKey = "radarr",
        defaultPort = 7878
    );

    companion object {
        fun allValue() = entries.toList()
    }
}
