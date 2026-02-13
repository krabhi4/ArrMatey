package com.dnfapps.arrmatey.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnfapps.arrmatey.arr.api.model.ArrMedia
import com.dnfapps.arrmatey.arr.api.model.Arrtist
import com.dnfapps.arrmatey.entensions.Bullet
import com.dnfapps.arrmatey.instances.model.InstanceType

@Composable
fun DetailsHeader(
    item: ArrMedia,
    type: InstanceType
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        DetailHeaderBanner(item.getBanner()?.remoteUrl)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 170.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PosterItem(
                item = item,
                modifier = Modifier.height(220.dp),
                aspectRatio = type.aspectRatio
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ClearLogo(item)

                if (item !is Arrtist) {
                    Text(
                        text = listOfNotNull(
                            item.year,
                            item.runtimeString,
                            item.certification
                        ).joinToString(Bullet),
                        fontSize = 16.sp
                    )
                    Text(
                        text = listOf(item.releasedBy, item.statusString).joinToString(Bullet),
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    )
                }
                Text(
                    text = item.genres.joinToString(Bullet),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}