package com.dnfapps.arrmatey.api.client

import com.dnfapps.arrmatey.model.Instance
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val HEADER_X_API_KEY = "X-Api-Key"

fun createInstanceClient(instance: Instance?) =
    HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }

        install(HttpTimeout) {
            if (instance?.slowInstance == true) {
                val customTimeout = instance.customTimeout?.let { it * 1000 } // convert seconds to millis
                val timeout = customTimeout ?: (5 * 60_000)
                requestTimeoutMillis = timeout
                socketTimeoutMillis = timeout
            }
        }

        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }

        instance?.let { instance ->
            defaultRequest {
                url(instance.url + "/")
                header(HEADER_X_API_KEY, instance.apiKey)
            }
        }
    }