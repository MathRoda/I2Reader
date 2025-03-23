package dev.mathroda.twelvereader.network.di

import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.network.NetworkService
import dev.mathroda.twelvereader.network.NetworkServiceImpl
import dev.mathroda.twelvereader.network.interceptor.OkhttpInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import org.koin.dsl.module

private const val BASE_URL = "https://api.elevenlabs.io"

val networkModule = module {
    val apiKey = MutableStateFlow("")
    val engine = OkHttp.create {
        addInterceptor(OkhttpInterceptor())
    }
    single { CoroutineScope(Dispatchers.Default + SupervisorJob()) }
    single<HttpClient> {
        val scope: CoroutineScope = get()
        val dataStoreManager: DataStoreManager = get()
        dataStoreManager.ElevenLabsApiKey().value
            .onEach { apiKey.emit(it) }
            .flowOn(Dispatchers.IO)
            .launchIn(scope)

        HttpClient(engine) {
            defaultRequest {
                url(BASE_URL)
                header("accept", "application/json")
                header("xi-api-key", apiKey.value)
            }

            install(ContentNegotiation) {
                json(
                    Json { ignoreUnknownKeys = true }
                )
            }
        }
    }
    single<NetworkService> { NetworkServiceImpl(get()) }
}