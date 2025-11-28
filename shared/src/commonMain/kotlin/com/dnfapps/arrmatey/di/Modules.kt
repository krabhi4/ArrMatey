package com.dnfapps.arrmatey.di

import com.dnfapps.arrmatey.Greeting
import com.dnfapps.arrmatey.api.arr.BaseArrClient
import com.dnfapps.arrmatey.database.ArrMateyDatabase
import com.dnfapps.arrmatey.database.getRoomDatabase
import com.dnfapps.arrmatey.ktor.demo.RocketComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

val networkModule = module {
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

    single { BaseArrClient() }

    single { RocketComponent() }
    single { Greeting() }
}

val databaseModule = module {
    single { getRoomDatabase(get()) }

    single { get<ArrMateyDatabase>().getInstanceDao() }
}

expect fun platformModule(): Module

fun appModules() = listOf(networkModule, databaseModule, platformModule())