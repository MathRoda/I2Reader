package dev.mathroda.twelvereader.ui.screens.mainplayer

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.infrastructure.mediaplayer.MyMediaPlayer
import dev.mathroda.twelvereader.infrastructure.mediaplayer.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

sealed interface MainPlayerUiActions {
    data class Play(val uri: String): MainPlayerUiActions
    data object Pause: MainPlayerUiActions
    data object Resume: MainPlayerUiActions
    data class SeekTo(val position: Long): MainPlayerUiActions
}

class MainPlayerViewModel(
    private val mediaPlayer: MyMediaPlayer
): ViewModel() {

    val playerState: StateFlow<PlayerState>
        get() = mediaPlayer.playerState

    private val _sliderPosition = MutableStateFlow(0L)
    val sliderPosition = _sliderPosition.asStateFlow()

    private var lastResumeTime: Long = 0L
    private var accumulatedTime: Long = 0L
    private var job: Job? = null

    fun onAction(action: MainPlayerUiActions) {
        when(action) {
            is MainPlayerUiActions.Play -> {
                initializeMedia(action.uri)
                resumeMedia()
            }
            is MainPlayerUiActions.Resume-> resumeMedia()
            is MainPlayerUiActions.Pause -> pauseMedia()
            is MainPlayerUiActions.SeekTo -> seekTo(action.position)
        }
    }

    fun initializeMedia(
        uri: String,
        playWhenReady: Boolean = false
    ) {
        resetTimer()
        val file = File(uri)
        mediaPlayer.setup(file, playWhenReady = playWhenReady)
    }

    private fun resumeMedia() {
        startTimer()
        mediaPlayer.resume()
    }

    private fun pauseMedia() {
        pauseTimer()
        mediaPlayer.pause()
    }

    private fun seekTo(position: Long) {
        updateSliderPosition(position)
        mediaPlayer.seekTo(position)
    }

    fun clearMediaPlayer() {
        resetTimer()
        mediaPlayer.release()
    }

    private fun startTimer() {
        if (lastResumeTime == 0L) {
            lastResumeTime = SystemClock.elapsedRealtime()
        }

        job = viewModelScope.launch {
            while (sliderPosition.value != playerState.value.totalDuration) {
                val elapsedTime = SystemClock.elapsedRealtime() - lastResumeTime
                val currentTime = (accumulatedTime + elapsedTime).coerceAtMost(playerState.value.totalDuration)
                updateSliderPosition(currentTime)
                delay(1000L) // Update timer every second
            }
        }
    }

    private fun pauseTimer() {
        job?.cancel()
        accumulatedTime += SystemClock.elapsedRealtime() - lastResumeTime
        lastResumeTime = 0L
    }

    private fun resetTimer() {
        job?.cancel()
        accumulatedTime = 0L
        lastResumeTime = 0L
        updateSliderPosition(0L)
    }

    private fun updateSliderPosition(position: Long) {
        accumulatedTime = position
        _sliderPosition.update { position }
        lastResumeTime = SystemClock.elapsedRealtime()
    }
}