package com.dnfapps.arrmatey.compose.utils

import com.dnfapps.arrmatey.api.arr.model.Language

fun List<Language>.singleLanguageLabel(): String {
    return when (this.size) {
        0 -> "Unknown"
        1 -> first().name ?: "Unknown"
        else -> "Multilingual"
    }
}