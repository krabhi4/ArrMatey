package com.dnfapps.arrmatey

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform