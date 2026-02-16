package com.dnfapps.arrmatey.entensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

fun Context.getDrawableId(key: String): Int {
    return resources.getIdentifier(key, "drawable", packageName)
}

@Composable
fun getDrawableId(key: String): Int {
    val context = LocalContext.current
    return context.getDrawableId(key)
}

fun Context.openLink(url: String) {
    val uri = url.toUri()
    try {
        val customTabIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setInstantAppsEnabled(true)
            .build()
        customTabIntent.launchUrl(this, uri)
    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}