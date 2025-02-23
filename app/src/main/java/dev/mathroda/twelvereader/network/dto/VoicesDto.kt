package dev.mathroda.twelvereader.network.dto


import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoicesDto(
    @SerialName("has_more")
    val hasMore: Boolean? = null,
    @SerialName("last_sort_id")
    val lastSortId: String? = null,
    @SerialName("voices")
    val voices: List<VoiceDto> = emptyList()
)