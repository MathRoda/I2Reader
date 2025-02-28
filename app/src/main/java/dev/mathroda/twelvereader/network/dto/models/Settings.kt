package dev.mathroda.twelvereader.network.dto.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    @SerialName("similarity_boost")
    val similarityBoost: Double? = null,
    @SerialName("stability")
    val stability: Double? = null,
    @SerialName("style")
    val style: Double? = null,
    @SerialName("use_speaker_boost")
    val useSpeakerBoost: Boolean? = null
)