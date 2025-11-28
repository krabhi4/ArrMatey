package com.dnfapps.arrmatey.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dnfapps.arrmatey.R
import com.dnfapps.arrmatey.compose.screens.viewmodel.AddInstanceScreenViewModel
import com.dnfapps.arrmatey.entensions.getDrawableId
import com.dnfapps.arrmatey.entensions.getString
import com.dnfapps.arrmatey.model.InstanceType

@Composable
fun InstanceInfoCard(instanceType: InstanceType) {
    val addInstanceViewModel = viewModel<AddInstanceScreenViewModel>()

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(getDrawableId(instanceType.iconKey)),
                    contentDescription = instanceType.toString(),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = instanceType.toString(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { addInstanceViewModel.dismissInfoCard(instanceType) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
            Text(
                text = getString(instanceType.descriptionKey),
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.github))
                }
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.website))
                }
            }
        }
    }
}