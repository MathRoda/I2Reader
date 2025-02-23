package dev.mathroda.twelvereader.network

import dev.mathroda.twelvereader.network.dto.VoicesDto
import dev.mathroda.twelvereader.network.utils.handleErrors
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NetworkServiceImpl(private val client: HttpClient): NetworkService {
    override suspend fun getSharedVoices(
        pageSize: Int,
        language: String
    ): VoicesDto {
        return handleErrors {
            client.get("/v1/shared-voices") {
                parameter("page_size", pageSize)
                parameter("language", language)
            }.body()
        }
    }
}