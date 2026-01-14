package com.dnfapps.arrmatey.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.api.arr.model.HistoryItem
import com.dnfapps.arrmatey.compose.utils.singleLanguageLabel
import com.dnfapps.arrmatey.entensions.bullet
import com.dnfapps.arrmatey.utils.format
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun HistoryItemView(
    item: HistoryItem
) {
    Card(
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 18.dp)
        ) {
            Text(
                text = item.eventType.name,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.added_on, item.date.format("MMM d, yyyy")),
                fontSize = 14.sp
            )
            Text(
                text = item.displayTitle ?: "---",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            val subLabel = buildString {
                append(item.quality.qualityLabel)
                bullet()
                append(item.languages.singleLanguageLabel())
                item.indexerLabel?.let { indexerLabel ->
                    bullet()
                    append(indexerLabel)
                }
            }
            Text(
                text = subLabel,
                fontSize = 14.sp
            )
        }
    }
}