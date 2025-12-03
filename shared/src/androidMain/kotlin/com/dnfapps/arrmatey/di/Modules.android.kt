package com.dnfapps.arrmatey.di

import androidx.room.RoomDatabase
import com.dnfapps.arrmatey.database.ArrMateyDatabase
import com.dnfapps.arrmatey.database.getDatabaseBuilder
import org.koin.dsl.module

val androidDbModule = module {
    single<RoomDatabase.Builder<ArrMateyDatabase>> {
        getDatabaseBuilder(get())
    }
}

actual fun platformModules() = listOf(androidDbModule)