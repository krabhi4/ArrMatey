package com.dnfapps.arrmatey.arr.state

import com.dnfapps.arrmatey.compose.utils.ReleaseFilterBy
import com.dnfapps.arrmatey.compose.utils.ReleaseSortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder

data class InteractiveSearchUiState(
    val sortBy: ReleaseSortBy = ReleaseSortBy.Weight,
    val sortOrder: SortOrder = SortOrder.Asc,
    val filterBy: ReleaseFilterBy = ReleaseFilterBy.Any
) {
    companion object {
        fun empty(filterBy: ReleaseFilterBy) = InteractiveSearchUiState(
            filterBy = filterBy
        )
    }
}