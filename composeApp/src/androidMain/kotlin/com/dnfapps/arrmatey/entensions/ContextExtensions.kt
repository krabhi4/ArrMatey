package com.dnfapps.arrmatey.entensions

import android.content.Context

fun Context.getString(key: String): String {
    val id = resources.getIdentifier(key, "string", packageName)
    return getString(id)
}