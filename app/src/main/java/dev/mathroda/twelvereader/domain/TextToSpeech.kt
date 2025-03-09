
package dev.mathroda.twelvereader.domain

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class TextToSpeech(
    val decodedAudio: ByteArray
) {
    suspend fun createMp3File(
        context: Context,
        fileName: String = "text_to_speech",
    ): File {
        return withContext(Dispatchers.IO) {
            val file = File.createTempFile(fileName, ".mp3", context.cacheDir)
            file.outputStream().use { it.write(decodedAudio) }
            file
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextToSpeech

        return decodedAudio.contentEquals(other.decodedAudio)
    }

    override fun hashCode(): Int {
        return decodedAudio.contentHashCode()
    }
}
