package dev.mathroda.twelvereader.network.dto.models


import dev.mathroda.twelvereader.domain.CharTiming
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Alignment(
    @SerialName("character_end_times_seconds")
    val characterEndTimesSeconds: List<Double?>? = null,
    @SerialName("character_start_times_seconds")
    val characterStartTimesSeconds: List<Double?>? = null,
    @SerialName("characters")
    val characters: List<Char>? = null
)

fun Alignment.toCharsTiming(): List<CharTiming> {
    if (characters == null || characterEndTimesSeconds == null || characterStartTimesSeconds == null) return emptyList()

    return characters.mapIndexed { index, char ->
        CharTiming(
            char = char,
            startTime = characterStartTimesSeconds[index]?.toLong(),
            endTime = characterEndTimesSeconds[index]?.toLong()
        )
    }
}