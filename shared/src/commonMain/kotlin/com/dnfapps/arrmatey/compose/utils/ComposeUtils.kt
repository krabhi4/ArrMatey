package com.dnfapps.arrmatey.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun requiredStringResource(resource: StringResource, required: Boolean = true): AnnotatedString {
    val string = stringResource(resource)
    return buildAnnotatedString {
        if (required) {
            withStyle(style = SpanStyle(color = Color.Red)) {
                append("* ")
            }
        }
        append(string)
    }
}