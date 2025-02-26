package dev.mathroda.twelvereader.network

import dev.mathroda.twelvereader.network.dto.VoiceDetailsDto
import dev.mathroda.twelvereader.network.dto.VoicesDto
import dev.mathroda.twelvereader.network.utils.handleErrors
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class NetworkServiceImpl(private val client: HttpClient): NetworkService {
    override suspend fun getSharedVoices(
        pageSize: Int,
        language: String
    ): VoicesDto {
        return handleErrors {
            client.get("/v1/voices").body()
        }
    }

    override suspend fun getVoiceById(voiceId: String): VoiceDetailsDto {
        return handleErrors {
            client.get("/v1/voices/$voiceId")
                .body()
        }
    }
}