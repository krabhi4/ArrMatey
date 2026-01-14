package com.dnfapps.arrmatey.ui.tabs

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.api.arr.model.QueueDownloadState
import com.dnfapps.arrmatey.api.arr.model.QueueItem
import com.dnfapps.arrmatey.api.arr.model.RadarrQueueItem
import com.dnfapps.arrmatey.api.arr.model.SonarrQueueItem
import com.dnfapps.arrmatey.api.client.ActivityQueue
import com.dnfapps.arrmatey.compose.utils.QueueSortBy
import com.dnfapps.arrmatey.compose.utils.SortOrder
import com.dnfapps.arrmatey.compose.utils.applySorting
import com.dnfapps.arrmatey.compose.utils.bytesAsFileSizeString
import com.dnfapps.arrmatey.compose.utils.filterBy
import com.dnfapps.arrmatey.database.InstanceRepository
import com.dnfapps.arrmatey.entensions.bullet
import com.dnfapps.arrmatey.entensions.copy
import com.dnfapps.arrmatey.entensions.getString
import com.dnfapps.arrmatey.ui.components.DropdownPicker
import com.dnfapps.arrmatey.utils.format
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ActivityTab() {
    val queueItems by ActivityQueue.items.collectAsStateWithLifecycle()

    var instanceFilterId by remember { mutableStateOf<Long?>(null) }
    var sortOrder by remember { mutableStateOf(SortOrder.Asc) }
    var sortBy by remember { mutableStateOf(QueueSortBy.Added) }

    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<QueueItem?>(null) }

    val allItems: List<QueueItem> by remember {
        derivedStateOf {
            queueItems
                .flatMap { it.value }
                .groupBy { it.taskGroup }
                .map { (_, groupItems) ->
                    val first = groupItems.first()
                    if (groupItems.size > 1) {
                        when (first) {
                            is SonarrQueueItem -> first.copy(taskGroupCount = groupItems.size)
                            is RadarrQueueItem -> first.copy(taskGroupCount = groupItems.size)
                        }
                    } else {
                        first
                    }
                }
                .filterBy(instanceFilterId)
                .applySorting(sortBy, sortOrder)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val countText = if (allItems.isNotEmpty()) " (${allItems.size})" else ""
                    Text(stringResource(R.string.activity) + countText)
                },
                actions = {
                    if (queueItems.isNotEmpty()) {
                        IconButton(
                            onClick = { showFilterSheet = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(R.string.filter)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues.copy(bottom = 0.dp))) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                items(items = allItems) { item ->
                    ActivityItem(item) {
                        selectedItem = item
                    }
                }
            }
        }

        if (showFilterSheet) {
            FilterSheet(
                onDismiss = { showFilterSheet = false },
                selectedInstanceId = instanceFilterId,
                onInstanceChange = { instanceFilterId = it },
                sortBy = sortBy,
                onSortByChanged = { sortBy = it },
                sortOrder = sortOrder,
                onSortOrderChanged = { sortOrder = it }
            )
        }

        selectedItem?.let { item ->
            QueueItemInfoSheet(
                item = item,
                onDismiss = { selectedItem = null }
            )
        }
    }
}

@Composable
fun ActivityItem(item: QueueItem, onClick: () -> Unit) {
    val colors = if (item.hasIssue) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    } else CardDefaults.cardColors()

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = colors
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.titleLabel,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val statusRow = buildString {
                    append(item.statusLabel)
                    if (item.trackedDownloadState == QueueDownloadState.Downloading) {
                        bullet()
                        append(item.progressLabel)
                        item.remainingTimeLabel?.let { remainingTimeLabel ->
                            bullet()
                            append(remainingTimeLabel)
                        }
                    }
                }
                Text(
                    text = statusRow,
                    fontSize = 14.sp
                )
            }

            if (item.hasIssue) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    onDismiss: () -> Unit,
    selectedInstanceId: Long?,
    onInstanceChange: (Long?) -> Unit,
    sortBy: QueueSortBy,
    onSortByChanged: (QueueSortBy) -> Unit,
    sortOrder: SortOrder,
    onSortOrderChanged: (SortOrder) -> Unit,
    instanceRepository: InstanceRepository = koinInject<InstanceRepository>()
) {
    val instances by instanceRepository.allInstancesFlow.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            DropdownPicker(
                options = instances.map { it.id },
                selectedOption = selectedInstanceId,
                onOptionSelected = onInstanceChange,
                getOptionLabel = { id -> instances.first { it.id == id }.label },
                label = { Text(stringResource(R.string.instances)) },
                includeAllOption = true,
                onAllSelected = { onInstanceChange(null) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DropdownPicker(
                    options = QueueSortBy.entries,
                    selectedOption = sortBy,
                    onOptionSelected = onSortByChanged,
                    label = { Text(stringResource(R.string.sort_by)) },
                    getOptionLabel = { it.name },
                    modifier = Modifier.weight(1f)
                )
                DropdownPicker(
                    options = SortOrder.entries,
                    selectedOption = sortOrder,
                    onOptionSelected = onSortOrderChanged,
                    label = { Text(stringResource(R.string.sort_order)) },
                    getOptionLabel = { getString(it.iosText) },
                    getOptionIcon = { it.androidIcon },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun QueueItemInfoSheet(
    onDismiss: () -> Unit,
    item: QueueItem
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = item.titleLabel,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = item.title ?: stringResource(R.string.unknown),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            val statusRow = buildAnnotatedString {
                withStyle(SpanStyle(
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium
                )) {
                    append(item.statusLabel)
                }
                bullet()
                append(item.quality.qualityLabel)
                bullet()
                append(item.size.toLong().bytesAsFileSizeString())
            }

            Text(text = statusRow)

            val chipItems = listOfNotNull(
                item.scoreLabel
            ) + item.customFormats.map { it.name }
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            ) {
                chipItems.forEach { chipItem ->
                    Box(
                        modifier = Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(8.dp))
                    ) {
                        Text(chipItem,
                            modifier = Modifier.padding(vertical = 2.dp, horizontal = 6.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item.errorMessage?.let { errorMessage ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(errorMessage)
                }
            } ?: item.statusMessages.forEach { status ->
                Card {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = status.title ?: "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        status.messages.forEach { message ->
                            Text(
                                text = message,
                                fontSize = 14.sp,
                                lineHeight = 16.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }

            val infoItems = mapOf(
                R.string.protocol to item.protocol.name,
                R.string.download_client to item.downloadClient,
                R.string.indexer to item.indexer,
                R.string.langauges to item.languageLabels.takeUnless { it.isEmpty() }?.joinToString(", "),
                R.string.added to item.added.format(),
                R.string.destination to item.outputPath
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
                modifier = Modifier.wrapContentWidth()
            ) {
                infoItems.forEach { (key, value) ->
                    value?.let {
                        item {
                            Text(text = stringResource(key), fontWeight = FontWeight.SemiBold)
                        }
                        item {
                            Text(text = value, fontSize = 14.sp)
                        }
                    }
                }
            }

            // todo - add options to remove + manual import
        }
    }
}