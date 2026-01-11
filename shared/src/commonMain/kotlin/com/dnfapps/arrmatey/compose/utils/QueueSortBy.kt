package com.dnfapps.arrmatey.compose.utils

import com.dnfapps.arrmatey.api.arr.model.QueueItem

enum class QueueSortBy {
    Title,
    Added
}

fun List<QueueItem>.applySorting(sortBy: QueueSortBy, sortOrder: SortOrder) = when(sortBy) {
    QueueSortBy.Title -> sortedBy { it.titleLabel }
    QueueSortBy.Added -> sortedBy { it.added }
}.let { when(sortOrder) {
    SortOrder.Asc -> it
    SortOrder.Desc -> it.reversed()
} }

fun List<QueueItem>.filterBy(instanceId: Long?) = when(instanceId) {
    null -> this
    else -> filter { item -> item.instanceId == instanceId }
}