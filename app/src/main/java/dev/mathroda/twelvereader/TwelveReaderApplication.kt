package dev.mathroda.twelvereader

import android.app.Application
import dev.mathroda.twelvereader.network.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TwelveReaderApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TwelveReaderApplication)
            modules(
                networkModule
            )
        }
    }
}