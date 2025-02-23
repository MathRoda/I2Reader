package dev.mathroda.twelvereader.network.dto


import dev.mathroda.twelvereader.domain.Voice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoiceDto(
    @SerialName("accent")
    val accent: String? = null,
    @SerialName("age")
    val age: String? = null,
    @SerialName("category")
    val category: String? = null,
    @SerialName("cloned_by_count")
    val clonedByCount: Int? = null,
    @SerialName("date_unix")
    val dateUnix: Int? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("descriptive")
    val descriptive: String? = null,
    @SerialName("featured")
    val featured: Boolean? = null,
    @SerialName("free_users_allowed")
    val freeUsersAllowed: Boolean? = null,
    @SerialName("gender")
    val gender: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("instagram_username")
    val instagramUsername: String? = null,
    @SerialName("language")
    val language: String? = null,
    @SerialName("live_moderation_enabled")
    val liveModerationEnabled: Boolean? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("notice_period")
    val noticePeriod: Int? = null,
    @SerialName("play_api_usage_character_count_1y")
    val playApiUsageCharacterCount1y: Int? = null,
    @SerialName("preview_url")
    val previewUrl: String? = null,
    @SerialName("public_owner_id")
    val publicOwnerId: String? = null,
    @SerialName("rate")
    val rate: Double? = null,
    @SerialName("tiktok_username")
    val tiktokUsername: String? = null,
    @SerialName("twitter_username")
    val twitterUsername: String? = null,
    @SerialName("usage_character_count_1y")
    val usageCharacterCount1y: Int? = null,
    @SerialName("usage_character_count_7d")
    val usageCharacterCount7d: Int? = null,
    @SerialName("use_case")
    val useCase: String? = null,
    @SerialName("voice_id")
    val voiceId: String? = null,
    @SerialName("youtube_username")
    val youtubeUsername: String? = null
)

fun VoiceDto.toVoice(): Voice {
    if (voiceId == null) {
        throw Throwable("Voice ID cannot be null")
    }

    return Voice(
        voiceId = voiceId,
        name = name ?: "",
        imageUrl = imageUrl ?: "",
        age = age ?: "",
        accent = accent?: "",
        gender = gender ?: "",
        description = description ?: "",
        previewUrl = previewUrl ?: ""
    )

}