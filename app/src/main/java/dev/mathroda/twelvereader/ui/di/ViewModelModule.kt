package dev.mathroda.twelvereader.ui.di

import dev.mathroda.twelvereader.ui.MainViewModel
import dev.mathroda.twelvereader.ui.screens.home.HomeViewModel
import dev.mathroda.twelvereader.ui.screens.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { OnboardingViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
}