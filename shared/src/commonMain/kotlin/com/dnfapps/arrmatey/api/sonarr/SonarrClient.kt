package com.dnfapps.arrmatey.api.sonarr

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.headers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SonarrClient : KoinComponent {

    private val httpClient: HttpClient by inject()

    suspend fun test(endpoint: String, apiKey: String): Boolean {
        try {
            val response = httpClient.get("$endpoint/api") {
                header("X-Api-Key", apiKey)
            }
            return response.status.value == 200
        } catch (e: Exception) {
            return false
        }
    }
}