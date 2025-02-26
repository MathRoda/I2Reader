package dev.mathroda.twelvereader.network.dto


import kotlinx.serialization.Serializable

@Serializable
data class VoicesDto(
    val voices: List<VoiceDetailsDto> = emptyList()
)