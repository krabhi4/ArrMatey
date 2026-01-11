package com.dnfapps.arrmatey

import com.dnfapps.arrmatey.model.InstanceType
import com.dnfapps.arrmatey.navigation.ArrTabNavigation
import com.dnfapps.arrmatey.navigation.MoviesTabNavigation
import com.dnfapps.arrmatey.navigation.SeriesTabNavigation
import com.dnfapps.arrmatey.navigation.SettingsNavigation
import org.koin.dsl.module

val androidModule = module {
    single { SettingsNavigation() }

    single { SeriesTabNavigation() }
    single { MoviesTabNavigation() }

    factory<ArrTabNavigation> { (type: InstanceType) ->
        when (type) {
            InstanceType.Radarr -> get<MoviesTabNavigation>()
            InstanceType.Sonarr -> get<SeriesTabNavigation>()
        }
    }
}