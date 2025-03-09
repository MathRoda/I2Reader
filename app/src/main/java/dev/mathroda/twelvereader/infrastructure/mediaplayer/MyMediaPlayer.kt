package dev.mathroda.twelvereader.infrastructure.mediaplayer

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

enum class MediaPlayerState {
    IDLE, PLAYING, PAUSED, ENDED
}

data class PlayerState(
    val current: MediaPlayerState = MediaPlayerState.IDLE,
    val totalDuration: Long = 0L
)

class MyMediaPlayer(
    private val context: Context
) {
    private var listener: Player.Listener? = null
    private var mediaPlayer: ExoPlayer? = null

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    val totalDuration: Long
        get() = mediaPlayer?.duration?.coerceAtLeast(0) ?: 0

    val currentPosition: Long
        get() = mediaPlayer?.currentPosition?.coerceAtLeast(0) ?: 0


    fun setup(
        vararg url: String,
        playWhenReady: Boolean = true,
        speed: Float = 1f,
    ) {
        initialize(
            playWhenReady = playWhenReady,
            speed = speed
        ) {
            val mediaItem = url.map { MediaItem.fromUri(it) }
            addMediaItems(mediaItem)
        }
    }

    fun setup(
        vararg file: File,
        playWhenReady: Boolean = true,
        speed: Float = 1f,
    ) {
        initialize(
            playWhenReady = playWhenReady,
            speed = speed
        ) {
            val mediaItem = file.map {  MediaItem.fromUri(it.toUri()) }
            addMediaItems(mediaItem)
        }
    }

    private fun initialize(
        playWhenReady: Boolean = true,
        speed: Float = 1f,
        setup: ExoPlayer.() -> Unit
    ) {
        listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                with(player) {
                    _playerState.update {
                        it.copy(
                            current = playbackState.toPlayerState(isPlaying),
                            totalDuration = duration.coerceAtLeast(0L)
                        )
                    }
                }
            }
        }

        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }

        mediaPlayer = ExoPlayer.Builder(context)
            .build()
            .apply {
                listener?.let { addListener(it) }
                addAnalyticsListener(EventLogger())
                setup()
                setPlaybackSpeed(speed)
                prepare()
                setPlayWhenReady(playWhenReady)
            }

        Log.d(TAG, "initialized")

    }


    fun resume() {
        mediaPlayer?.play()
        Log.d(TAG, "resumed")
    }


    fun stop() {
        mediaPlayer?.stop()
        Log.d(TAG, "stopped")
    }

    fun pause() {
        mediaPlayer?.pause()
        Log.d(TAG, "paused")
    }

    fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position)
    }

    fun setPlaybackSpeed(speed: Float) {
        mediaPlayer?.setPlaybackSpeed(speed)
    }

    fun release() {
        mediaPlayer?.apply {
            listener?.let { removeListener(it) }
            this.release()
        }
        mediaPlayer = null
        listener = null
        Log.d(TAG, "released")
    }

    private fun Int.toPlayerState(isPlaying: Boolean): MediaPlayerState = when (this) {
        Player.STATE_IDLE -> MediaPlayerState.IDLE
        Player.STATE_ENDED -> MediaPlayerState.ENDED
        else -> if (isPlaying) MediaPlayerState.PLAYING else MediaPlayerState.PAUSED
    }

    companion object {
        const val TAG = "MyMediaPlayer"
    }
}