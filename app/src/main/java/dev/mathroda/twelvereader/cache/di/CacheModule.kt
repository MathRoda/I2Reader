package dev.mathroda.twelvereader.cache.di

import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val cacheModule = module {
    single { DataStoreManager(androidApplication()) }
}