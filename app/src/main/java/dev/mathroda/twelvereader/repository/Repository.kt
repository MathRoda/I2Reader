package dev.mathroda.twelvereader.repository

import dev.mathroda.twelvereader.domain.TextToSpeech
import dev.mathroda.twelvereader.domain.Voice
import dev.mathroda.twelvereader.utils.Resource
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getVoices(
        pageSize: Int = 10,
        language: String = "en"
    ): Flow<Resource<List<Voice>>>

    fun getVoiceById(voiceId: String): Flow<Resource<Voice>>

    suspend fun textToSpeech(
        text: String,
        voiceId: String
    ): TextToSpeech
}