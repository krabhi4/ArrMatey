package com.dnfapps.arrmatey.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "instances")
data class Instance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: InstanceType,
    var label: String,
    var url: String,
    var apiKey: String,
    var enabled: Boolean = true
)

enum class InstanceType {
    Sonarr
}
