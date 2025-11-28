package com.dnfapps.arrmatey.utils

fun String.isValidUrl(): Boolean {
    // Check if URL starts with http:// or https://
    if (!startsWith("http://") && !startsWith("https://")) {
        return false
    }

    // Regular expression pattern for URL validation
    // Matches http(s)://domain(:port)?(/path)?
    val urlPattern = Regex(
        "^https?://([a-zA-Z0-9.-]+)(:[0-9]{1,5})?(/.*)?$"
    )

    // Check if URL matches the pattern
    if (!urlPattern.matches(this)) {
        return false
    }

    // Extract and validate port if present
    val portPattern = Regex(":([0-9]{1,5})")
    val portMatch = portPattern.find(this)

    if (portMatch != null) {
        val port = portMatch.groupValues[1].toInt()
        // Port must be between 1 and 65535
        if (port < 1 || port > 65535) {
            return false
        }
    }

    return true
}