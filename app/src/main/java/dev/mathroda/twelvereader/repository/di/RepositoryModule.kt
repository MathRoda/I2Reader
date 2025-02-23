package dev.mathroda.twelvereader.repository.di

import dev.mathroda.twelvereader.repository.Repository
import dev.mathroda.twelvereader.repository.RepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<Repository> { RepositoryImpl(get()) }
}