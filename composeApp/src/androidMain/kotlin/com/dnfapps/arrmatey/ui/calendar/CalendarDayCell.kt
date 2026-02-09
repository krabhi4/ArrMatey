package com.dnfapps.arrmatey.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnfapps.arrmatey.extensions.localToday
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun CalendarDayCell(
    date: LocalDate,
    isSelected: Boolean,
    movieCount: Int,
    episodeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { Clock.localToday() }
    val isToday = date == today

    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        onClick = onClick,
        color = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isToday -> MaterialTheme.colorScheme.primaryContainer
            else -> Color.Transparent
        },
        shape = RoundedCornerShape(8.dp),
        border = if (isToday && !isSelected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.weight(1f))

            if (movieCount > 0 || episodeCount > 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (movieCount > 0) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = CircleShape,
                            modifier = Modifier.size(16.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (movieCount > 9) "9+" else movieCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                    if (episodeCount > 0) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = CircleShape,
                            modifier = Modifier.size(16.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (episodeCount > 9) "9+" else episodeCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}