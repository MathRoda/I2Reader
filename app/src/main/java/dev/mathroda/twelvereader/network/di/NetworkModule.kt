package dev.mathroda.twelvereader.network.di

import dev.mathroda.twelvereader.network.interceptor.OkhttpInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

import org.koin.dsl.module

private const val BASE_URL = "https://api.elevenlabs.io/v1/"
private val API_KEY: String = TODO("Please enter your API Key")

val networkModule = module {
    val engine = OkHttp.create {
        addInterceptor(OkhttpInterceptor())
    }

    single<HttpClient> {
        HttpClient(engine) {
            defaultRequest {
                url(BASE_URL)
                header("accept", "application/json")
                header("xi-api-key", API_KEY)
            }

            install(ContentNegotiation) {
                json(
                    Json { ignoreUnknownKeys = true }
                )
            }
        }
    }
}