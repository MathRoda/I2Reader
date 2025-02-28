@file:OptIn(ExperimentalEncodingApi::class)

package dev.mathroda.twelvereader.infrastructure.mediaplayer

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import java.io.File
import kotlin.io.encoding.ExperimentalEncodingApi


class MyMediaPlayer(
     private val context: Context
) {
    private lateinit var mediaPlayer: ExoPlayer

    fun setupUrl(
        vararg urls: String,
        playWhenReady: Boolean = true
    ) {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
        }

        mediaPlayer = ExoPlayer
            .Builder(context)
            .build()
            .apply {
                val mediaItems = urls.map { MediaItem.fromUri(it) }
                setMediaItems(mediaItems)
                prepare()
                setPlayWhenReady(playWhenReady)
            }
    }

    fun setupFile(
        file: File,
        playWhenReady: Boolean = true
    ) {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
        }

        mediaPlayer = ExoPlayer
            .Builder(context)
            .build()
            .apply {
                val mediaItems = MediaItem.fromUri(file.toUri())
                setMediaItem(mediaItems)
                prepare()
                setPlayWhenReady(playWhenReady)
            }
    }

    fun play() {
        if (mediaPlayer.isPlaying) return
        mediaPlayer.play()
    }

    fun pause() {
        if (!mediaPlayer.isPlaying) return
        mediaPlayer.play()
    }

    fun stop() {
        if (!mediaPlayer.isPlaying) return
        mediaPlayer.stop()
    }

    fun release() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}