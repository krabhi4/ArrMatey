package com.dnfapps.arrmatey.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dnfapps.arrmatey.api.arr.model.ExtraFile

@Composable
fun ExtraFileCard(extraFile: ExtraFile) {
    ContainerCard {
        Text(
            text = extraFile.relativePath,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = extraFile.type.name,
            fontSize = 14.sp
        )
    }
}