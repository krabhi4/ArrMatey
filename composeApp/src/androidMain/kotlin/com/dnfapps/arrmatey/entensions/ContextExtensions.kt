package com.dnfapps.arrmatey.entensions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import org.jetbrains.compose.resources.DrawableResource

fun Context.getString(key: String): String {
    val id = resources.getIdentifier(key, "string", packageName)
    return getString(id)
}

@Composable
fun getString(key: String): String {
    val context = LocalContext.current
    return context.getString(key)
}

fun Context.getDrawableId(key: String): Int {
    return resources.getIdentifier(key, "drawable", packageName)
}

@Composable
fun getDrawableId(key: String): Int {
    val context = LocalContext.current
    return context.getDrawableId(key)
}