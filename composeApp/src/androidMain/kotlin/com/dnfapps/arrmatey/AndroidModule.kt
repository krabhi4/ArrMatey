package com.dnfapps.arrmatey

import coil3.ImageLoader
import com.dnfapps.arrmatey.navigation.MoviesTabNavigation
import com.dnfapps.arrmatey.navigation.MusicTabNavigation
import com.dnfapps.arrmatey.navigation.NavigationManager
import com.dnfapps.arrmatey.navigation.SeriesTabNavigation
import com.dnfapps.arrmatey.navigation.SettingsNavigation
import com.dnfapps.arrmatey.ui.helpers.ArrImageLoader
import org.koin.dsl.module

val androidModule = module {
    single { SettingsNavigation() }

    single { SeriesTabNavigation() }
    single { MoviesTabNavigation() }
    single { MusicTabNavigation() }

    single { NavigationManager(get(), get(), get(), get()) }

    single<ImageLoader> {
        ArrImageLoader(get(), get())
            .imageLoader
    }
}