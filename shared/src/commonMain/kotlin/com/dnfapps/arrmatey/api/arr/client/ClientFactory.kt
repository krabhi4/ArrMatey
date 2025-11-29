package com.dnfapps.arrmatey.api.arr.client

import com.dnfapps.arrmatey.model.Instance
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.headers
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
            // todo - store this in instance and allow user to config
            requestTimeoutMillis = 5 * 60_000
            socketTimeoutMillis = 5 * 60_000
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