package com.dnfapps.arrmatey.di

import com.dnfapps.arrmatey.api.arr.BaseArrClient
import com.dnfapps.arrmatey.api.arr.SonarrClient
import com.dnfapps.arrmatey.api.arr.client.createInstanceClient
import com.dnfapps.arrmatey.database.ArrMateyDatabase
import com.dnfapps.arrmatey.database.getRoomDatabase
import com.dnfapps.arrmatey.model.Instance
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

val networkModule = module {
    factory<HttpClient> { (instance: Instance?) -> createInstanceClient(instance) }

    single { BaseArrClient() }
    factory<SonarrClient> { (instance: Instance) -> SonarrClient(instance) }
}

val databaseModule = module {
    single { getRoomDatabase(get()) }

    single { get<ArrMateyDatabase>().getInstanceDao() }
}

expect fun platformModule(): Module

fun appModules() = listOf(networkModule, databaseModule, platformModule())