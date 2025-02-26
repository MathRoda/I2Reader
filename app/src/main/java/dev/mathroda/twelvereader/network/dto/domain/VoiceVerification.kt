package dev.mathroda.twelvereader.network.dto.domain


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoiceVerification(
    @SerialName("is_verified")
    val isVerified: Boolean? = null,
    @SerialName("language")
    val language: String? = null,
    @SerialName("requires_verification")
    val requiresVerification: Boolean? = null,
    @SerialName("verification_attempts_count")
    val verificationAttemptsCount: Int? = null,
    @SerialName("verification_failures")
    val verificationFailures: List<String>? = null
)