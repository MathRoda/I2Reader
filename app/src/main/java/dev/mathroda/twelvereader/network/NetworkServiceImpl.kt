package dev.mathroda.twelvereader.network

import dev.mathroda.twelvereader.network.dto.TextToSpeechDto
import dev.mathroda.twelvereader.network.dto.VoiceDetailsDto
import dev.mathroda.twelvereader.network.dto.VoicesDto
import dev.mathroda.twelvereader.network.utils.handleErrors
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

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

    override suspend fun postTextToSpeech(text: String, voiceId: String): TextToSpeechDto {
        return handleErrors {
            client.post("/v1/text-to-speech/$voiceId/with-timestamps") {
                contentType(ContentType.Application.Json)
                setBody("""{"text": "$text"}""")
            }.body()
        }
    }
}