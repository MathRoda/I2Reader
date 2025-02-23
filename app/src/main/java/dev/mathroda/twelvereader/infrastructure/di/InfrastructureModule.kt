package dev.mathroda.twelvereader.infrastructure.di

import dev.mathroda.twelvereader.infrastructure.mediaplayer.MyMediaPlayer
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val infrastructureModule = module {
    single { MyMediaPlayer(androidApplication()) }
}