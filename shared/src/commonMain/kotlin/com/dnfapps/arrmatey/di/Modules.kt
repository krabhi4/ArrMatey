package com.dnfapps.arrmatey.di

import com.dnfapps.arrmatey.api.arr.BaseArrClient
import com.dnfapps.arrmatey.api.arr.RadarrClient
import com.dnfapps.arrmatey.api.arr.SonarrClient
import com.dnfapps.arrmatey.api.client.createInstanceClient
import com.dnfapps.arrmatey.database.ArrMateyDatabase
import com.dnfapps.arrmatey.database.getRoomDatabase
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.utils.NetworkConnectivityObserverFactory
import com.dnfapps.arrmatey.utils.NetworkConnectivityRepository
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

val networkModule = module {
    factory<HttpClient> { (instance: Instance?) -> createInstanceClient(instance) }

    single { BaseArrClient() }
    factory<SonarrClient> { (instance: Instance) -> SonarrClient(instance) }
    factory<RadarrClient> { (instance: Instance) -> RadarrClient(instance) }

    single { NetworkConnectivityObserverFactory().create() }

    factory { NetworkConnectivityRepository() }
}

val databaseModule = module {
    single { getRoomDatabase(get()) }

    single { get<ArrMateyDatabase>().getInstanceDao() }
    single { get<ArrMateyDatabase>().getSeriesDao() }
    single { get<ArrMateyDatabase>().getMoviesDao() }
}

expect fun platformModule(): Module

fun appModules() = listOf(networkModule, databaseModule, platformModule())