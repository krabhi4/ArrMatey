package com.dnfapps.arrmatey.di

import com.dnfapps.arrmatey.Greeting
import com.dnfapps.arrmatey.api.sonarr.SonarrClient
import com.dnfapps.arrmatey.ktor.demo.RocketComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModules = module {
    single {
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

            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    single { SonarrClient() }

    single { RocketComponent() }
    single { Greeting() }
}

fun appModules() = listOf(networkModules)