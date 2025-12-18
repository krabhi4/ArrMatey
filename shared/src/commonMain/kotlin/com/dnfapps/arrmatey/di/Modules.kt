package com.dnfapps.arrmatey.di

import com.dnfapps.arrmatey.DataStoreFactory
import com.dnfapps.arrmatey.PreferencesStore
import com.dnfapps.arrmatey.api.arr.GenericClient
import com.dnfapps.arrmatey.api.arr.RadarrClient
import com.dnfapps.arrmatey.api.arr.SonarrClient
import com.dnfapps.arrmatey.api.arr.model.AnyArrMedia
import com.dnfapps.arrmatey.api.arr.viewmodel.BaseArrRepository
import com.dnfapps.arrmatey.api.arr.viewmodel.createInstanceRepository
import com.dnfapps.arrmatey.api.client.createInstanceClient
import com.dnfapps.arrmatey.compose.screens.viewmodel.AddInstanceRepository
import com.dnfapps.arrmatey.database.ArrMateyDatabase
import com.dnfapps.arrmatey.database.InstanceRepository
import com.dnfapps.arrmatey.database.getRoomDatabase
import com.dnfapps.arrmatey.model.Instance
import com.dnfapps.arrmatey.utils.NetworkConnectivityObserverFactory
import com.dnfapps.arrmatey.utils.NetworkConnectivityRepository
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

val networkModule = module {
    factory<HttpClient> { (instance: Instance?) -> createInstanceClient(instance) }

    single { GenericClient() }
    factory<SonarrClient> { (instance: Instance) -> SonarrClient(instance) }
    factory<RadarrClient> { (instance: Instance) -> RadarrClient(instance) }

    single { NetworkConnectivityObserverFactory().create() }
    single { NetworkConnectivityRepository() }
}

val databaseModule = module {
    single { getRoomDatabase(get()) }

    single { get<ArrMateyDatabase>().getInstanceDao() }

    single { InstanceRepository() }
    factory { AddInstanceRepository() }

    factory<BaseArrRepository<out AnyArrMedia>> {
        (instance: Instance) -> createInstanceRepository(instance)
    }
}

val appModule = module {
    single { DataStoreFactory().provideDataStore() }
    single { PreferencesStore() }
}

expect fun platformModules(): List<Module>

fun appModules() = listOf(networkModule, databaseModule, appModule) + platformModules()