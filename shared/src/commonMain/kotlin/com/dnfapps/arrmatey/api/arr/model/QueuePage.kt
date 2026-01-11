package com.dnfapps.arrmatey.api.arr.model

import kotlinx.serialization.Serializable

@Serializable
data class QueuePage(
    val page: Int,
    val pageSize: Int,
    val totalRecords: Int,
    val records: List<QueueItem>
)
