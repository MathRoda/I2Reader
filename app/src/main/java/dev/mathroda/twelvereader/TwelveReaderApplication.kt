package dev.mathroda.twelvereader

import android.app.Application
import dev.mathroda.twelvereader.cache.di.cacheModule
import dev.mathroda.twelvereader.infrastructure.di.infrastructureModule
import dev.mathroda.twelvereader.network.di.networkModule
import dev.mathroda.twelvereader.repository.di.repositoryModule
import dev.mathroda.twelvereader.ui.di.viewModelModule
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
                networkModule,
                cacheModule,
                infrastructureModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}