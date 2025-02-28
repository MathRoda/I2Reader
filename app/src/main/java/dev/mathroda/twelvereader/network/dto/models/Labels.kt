package dev.mathroda.twelvereader.network.dto.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Labels(
    @SerialName("accent")
    val accent: String? = null,
    @SerialName("age")
    val age: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("gender")
    val gender: String? = null,
    @SerialName("use_case")
    val useCase: String? = null
)