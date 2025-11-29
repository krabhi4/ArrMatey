package com.dnfapps.arrmatey.api.arr

import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.model.InstanceType
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

open class BaseArrClient(instance: Instance? = null): KoinComponent {

    protected val httpClient: HttpClient by inject { parametersOf(instance) }

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