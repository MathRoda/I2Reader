package dev.mathroda.twelvereader.ui.screens.mainplayer

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.domain.CharTiming
import dev.mathroda.twelvereader.infrastructure.mediaplayer.MyMediaPlayer
import dev.mathroda.twelvereader.infrastructure.mediaplayer.PlayerState
import dev.mathroda.twelvereader.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainPlayerViewModel(
    private val mediaPlayer: MyMediaPlayer,
    private val dataStoreManager: DataStoreManager,
    private val repository: Repository,
    private val context: Application
): ViewModel() {

    data class SpokenText(
        val text: String = "",
        val charsTiming: List<CharTiming> = emptyList()
    )

    private var uri: String = ""
    var shouldReinitialize = false

    val playerState: StateFlow<PlayerState>
        get() = mediaPlayer.playerState

    private val _sliderPosition = MutableStateFlow(0L)
    val sliderPosition = _sliderPosition.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _spokenText = MutableStateFlow(SpokenText())
    val spokenText = _spokenText.asStateFlow()

    val mediaSpeed: StateFlow<Float>
        get() = dataStoreManager.MediaSpeedDefault()
            .value
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                1f
            )

    val currentReader: Flow<String>
        get() = dataStoreManager.SelectedVoice()
            .value
            .map { it.name.split(" ")[0] }

    val colors =  listOf(
        Color(0xFF00B0FF), Color(0xFF00E5FF), Color(0xFF1DE9B6),
        Color(0xFF76FF03), Color(0xFFFFEA00), Color(0xFFFF9100),
        Color(0xFFFF1744), Color(0xFFD500F9), Color(0xFF6200EA)
    )

    init {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            while (isActive) {
                _sliderPosition.update { mediaPlayer.currentPosition }
                delay(100L)
            }
        }
    }

    fun onAction(action: MainPlayerUiActions) {
        when(action) {
            is MainPlayerUiActions.Play -> viewModelScope.launch { initializeMedia(uri, playWhenReady = true) }
            is MainPlayerUiActions.Resume-> resumeMedia()
            is MainPlayerUiActions.Pause -> pauseMedia()
            is MainPlayerUiActions.SeekTo -> seekTo(action.position)
            is MainPlayerUiActions.SetSpeed -> setPlaybackSpeed(action.speed)
        }
    }

    fun reInitializeMedia(
        didVoiceChange: Boolean,
        text: String
    ) {
        viewModelScope.launch {
            if (!didVoiceChange) {
                initializeMedia(uri)
                return@launch
            }

            val voice = withContext(Dispatchers.IO) {
                dataStoreManager.SelectedVoice().value.first()
            }
            postTextToSpeech(text, voice)
        }
    }

    private suspend fun initializeMedia(
        uri: String,
        playWhenReady: Boolean = false
    ) {
        val speed = withContext(Dispatchers.IO) {
            dataStoreManager.MediaSpeedDefault().value.first()
        }
        withContext(Dispatchers.Main) {
            updateSliderPosition(0L)
            val file = File(uri)
            mediaPlayer.setup(
                file,
                playWhenReady = playWhenReady,
                speed = speed
            )
        }
    }

    fun generateAudio(
        text: String
    ) {
        viewModelScope.launch {
            val voice = withContext(Dispatchers.IO) {
                dataStoreManager.SelectedVoice().value.first()
            }
            postTextToSpeech(text, voice, true)
        }
    }

    private suspend fun postTextToSpeech(
        text: String,
        voice: DataStoreManager.DataStoreVoice,
        playWhenReady: Boolean = false
    ) {
        try {
            updateIsLoading(true)
            val result =  repository.textToSpeech(text, voice.id)
            updateCharsTiming(result.charsTiming)
            val file = result.createMp3File(context)
            uri = file.absolutePath
            initializeMedia(uri, playWhenReady)
        }catch (e: Throwable){
            e.printStackTrace()
        } finally {
            updateIsLoading(false)
        }
    }

    private fun resumeMedia() {
        mediaPlayer.resume()
    }

    private fun pauseMedia() {
        mediaPlayer.pause()
    }

    private fun seekTo(position: Long) {
        updateSliderPosition(position)
        mediaPlayer.seekTo(position)
    }

    fun clearMediaPlayer() {
        mediaPlayer.release()
    }


    private fun setPlaybackSpeed(speed: Float) {
        mediaPlayer.setPlaybackSpeed(speed)
        updateMediaSpeed(speed)
    }

    private fun updateMediaSpeed(speed: Float) {
        viewModelScope.launch {
            dataStoreManager.MediaSpeedDefault().update(speed)
        }
    }

    private fun updateIsLoading(loading: Boolean)  {
        _isLoading.update { loading }
    }

    private fun updateSliderPosition(position: Long) {
        _sliderPosition.update { position }
    }

    fun updateText(text: String) {
        _spokenText.update {
            it.copy(text = text)
        }
    }

    private fun updateCharsTiming(
        charsTiming: List<CharTiming>
    ) {
        _spokenText.update {
            it.copy(charsTiming = charsTiming)
        }
    }
}