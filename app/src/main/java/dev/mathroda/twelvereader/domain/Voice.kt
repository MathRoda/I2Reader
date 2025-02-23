package dev.mathroda.twelvereader.domain

data class Voice(
    val voiceId: String,
    val name: String = "",
    val imageUrl: String = "",
    val age: String = "",
    val gender: String = "",
    val accent: String = "",
    val description: String = "",
    val previewUrl: String = "",
)
