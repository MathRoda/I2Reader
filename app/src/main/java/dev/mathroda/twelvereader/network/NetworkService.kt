package dev.mathroda.twelvereader.network

import dev.mathroda.twelvereader.network.dto.TextToSpeechDto
import dev.mathroda.twelvereader.network.dto.VoiceDetailsDto
import dev.mathroda.twelvereader.network.dto.VoicesDto

interface NetworkService {

    suspend fun getSharedVoices(
        pageSize: Int,
        language: String
    ): VoicesDto

    suspend fun getVoiceById(
        voiceId: String
    ): VoiceDetailsDto

    suspend fun postTextToSpeech(
        text: String,
        voiceId: String
    ): TextToSpeechDto
}