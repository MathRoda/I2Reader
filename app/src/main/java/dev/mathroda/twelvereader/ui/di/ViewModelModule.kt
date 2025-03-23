package dev.mathroda.twelvereader.ui.di

import dev.mathroda.twelvereader.domain.TextInMemoryStorage
import dev.mathroda.twelvereader.ui.MainViewModel
import dev.mathroda.twelvereader.ui.screens.home.HomeViewModel
import dev.mathroda.twelvereader.ui.screens.mainplayer.MainPlayerViewModel
import dev.mathroda.twelvereader.ui.screens.onboarding.OnboardingViewModel
import dev.mathroda.twelvereader.ui.screens.selectvoice.SelectVoiceViewModel
import dev.mathroda.twelvereader.ui.screens.setapikey.SetApiKeyViewModel
import dev.mathroda.twelvereader.ui.screens.writetext.WriteTextViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { TextInMemoryStorage() }
    viewModel { MainViewModel(get()) }
    viewModel { OnboardingViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { MainPlayerViewModel(get(), get(), get(), get(), context = androidApplication()) }
    viewModel { SelectVoiceViewModel(get(), get(), get()) }
    viewModel { WriteTextViewModel(get(), get(), get(), get()) }
    viewModel { SetApiKeyViewModel(get()) }
}