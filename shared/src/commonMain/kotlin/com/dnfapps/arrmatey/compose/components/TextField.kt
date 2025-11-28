package com.dnfapps.arrmatey.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

@Composable
fun AMOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    label: String? = null,
    description: String? = null,
    placeholder: String? = null,
    errorMessage: String? = null,
    isError: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        label?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                maxLines = 1
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder?.let { {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            } },
            singleLine = singleLine,
            isError = isError,
            supportingText = if (isError && errorMessage != null) {
                { Text(text = errorMessage) }
            } else null
        )
        description?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun AMOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    label: AnnotatedString? = null,
    description: String? = null,
    placeholder: String? = null,
    errorMessage: String? = null,
    isError: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        label?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                maxLines = 1
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder?.let { {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            } },
            singleLine = singleLine,
            isError = isError,
            supportingText = if (isError && errorMessage != null) {
                { Text(text = errorMessage) }
            } else null
        )
        description?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
        }
    }
}