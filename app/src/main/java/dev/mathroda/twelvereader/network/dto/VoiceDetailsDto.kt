package dev.mathroda.twelvereader.network.dto


import dev.mathroda.twelvereader.domain.Voice
import dev.mathroda.twelvereader.network.dto.models.FineTuning
import dev.mathroda.twelvereader.network.dto.models.Labels
import dev.mathroda.twelvereader.network.dto.models.Settings
import dev.mathroda.twelvereader.network.dto.models.VoiceVerification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoiceDetailsDto(
    @SerialName("available_for_tiers")
    val availableForTiers: List<String>? = null,
    @SerialName("category")
    val category: String? = null,
    @SerialName("created_at_unix")
    val createdAtUnix: Int? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("fine_tuning")
    val fineTuning: FineTuning? = null,
    @SerialName("high_quality_base_model_ids")
    val highQualityBaseModelIds: List<String?>? = null,
    @SerialName("is_legacy")
    val isLegacy: Boolean? = null,
    @SerialName("is_mixed")
    val isMixed: Boolean? = null,
    @SerialName("is_owner")
    val isOwner: Boolean? = null,
    @SerialName("labels")
    val labels: Labels? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("permission_on_resource")
    val permissionOnResource: String? = null,
    @SerialName("preview_url")
    val previewUrl: String? = null,
    @SerialName("settings")
    val settings: Settings? = null,
    @SerialName("voice_id")
    val voiceId: String? = null,
    @SerialName("voice_verification")
    val voiceVerification: VoiceVerification? = null
)

fun VoiceDetailsDto.toVoice(): Voice {
    if (voiceId == null) {
        throw Throwable("Voice ID cannot be null")
    }

    return Voice(
        voiceId = voiceId,
        name = name ?: "",
        imageUrl = "",
        age = labels?.age ?: "",
        accent = labels?.accent ?: "",
        gender = labels?.gender ?: "",
        description = description ?: "",
        previewUrl = previewUrl ?: ""
    )

}