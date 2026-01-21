package com.dnfapps.arrmatey

import com.dnfapps.arrmatey.navigation.MoviesTabNavigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.navigation.SeriesTabNavigation
import com.dnfapps.arrmatey.navigation.SettingsNavigation
import org.koin.dsl.module

val androidModule = module {
    single { SettingsNavigation() }

    single { SeriesTabNavigation() }
    single { MoviesTabNavigation() }

    single { NavigationManager(get(), get(), get()) }
}