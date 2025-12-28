package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class RootFolder(
    val id: Int,
    val path: String,
    val accessible: Boolean,
    val freeSpace: Long,
    val unmappedFolders: List<UnmappedFolder>
)
