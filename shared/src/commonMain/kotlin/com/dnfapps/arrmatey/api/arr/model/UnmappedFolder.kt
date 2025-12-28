package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class UnmappedFolder(
    val name: String,
    val path: String,
    val relativePath: String
)
