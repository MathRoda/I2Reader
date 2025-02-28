@file:OptIn(ExperimentalEncodingApi::class)

package dev.mathroda.twelvereader.network.dto


import dev.mathroda.twelvereader.domain.TextToSpeech
import dev.mathroda.twelvereader.network.dto.models.Alignment
import dev.mathroda.twelvereader.network.dto.models.NormalizedAlignment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class TextToSpeechDto(
    @SerialName("alignment")
    val alignment: Alignment? = null,
    @SerialName("audio_base64")
    val audioBase64: String? = null,
    @SerialName("normalized_alignment")
    val normalizedAlignment: NormalizedAlignment? = null
)

fun TextToSpeechDto.toTextToSpeech(): TextToSpeech {
    if (audioBase64 == null) {
        throw Throwable("Audio base64 cannot be null")
    }

    return TextToSpeech(
        decodedAudio = Base64.decode(audioBase64)
    )

}