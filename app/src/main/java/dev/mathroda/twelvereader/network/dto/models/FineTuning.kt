package dev.mathroda.twelvereader.network.dto.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FineTuning(
    @SerialName("dataset_duration_seconds")
    val datasetDurationSeconds: Double? = null,
    @SerialName("finetuning_state")
    val fineTuningState: String? = null,
    @SerialName("is_allowed_to_fine_tune")
    val isAllowedToFineTune: Boolean? = null,
    @SerialName("language")
    val language: String? = null,
    @SerialName("manual_verification")
    val manualVerification: Boolean? = null,
    @SerialName("manual_verification_requested")
    val manualVerificationRequested: Boolean? = null,
    @SerialName("verification_attempts_count")
    val verificationAttemptsCount: Int? = null,
)