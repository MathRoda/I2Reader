package dev.mathroda.twelvereader.network.dto.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NormalizedAlignment(
    @SerialName("character_end_times_seconds")
    val characterEndTimesSeconds: List<Double?>? = null,
    @SerialName("character_start_times_seconds")
    val characterStartTimesSeconds: List<Double?>? = null,
    @SerialName("characters")
    val characters: List<String?>? = null
)