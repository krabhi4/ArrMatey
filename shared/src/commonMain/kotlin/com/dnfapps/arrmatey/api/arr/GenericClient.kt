package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.api.client.NetworkResult
import com.dnfapps.arrmatey.api.client.safeGet
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class GenericClient: KoinComponent {

    private val httpClient: HttpClient by inject { parametersOf(null) }

    suspend fun test(endpoint: String, apiKey: String): Boolean {
        try {
            val response = httpClient.safeGet<Any>("$endpoint/api") {
                header("X-Api-Key", apiKey)
            }
            return response is NetworkResult.Success
        } catch (e: Exception) {
            return false
        }
    }
}