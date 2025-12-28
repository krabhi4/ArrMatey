package com.dnfapps.arrmatey

import android.app.Application
import com.dnfapps.arrmatey.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ArrMateyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ArrMateyApplication)
            modules(appModules() + listOf(androidModule))
        }
    }
}