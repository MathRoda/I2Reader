package dev.mathroda.twelvereader.ui.screens.selectvoice

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.domain.Voice
import dev.mathroda.twelvereader.infrastructure.mediaplayer.MyMediaPlayer
import dev.mathroda.twelvereader.repository.Repository
import dev.mathroda.twelvereader.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SelectVoiceViewModel(
    private val repository: Repository,
    private val datastore: DataStoreManager,
    private val mediaPlayer: MyMediaPlayer
): ViewModel() {

    private var currentPlayingVoice: String? = null

    private val _voices = MutableStateFlow<Resource<List<Voice>>>(Resource.Loading())
    val voices = _voices.asStateFlow()

    val selectedVoice: StateFlow<DataStoreManager.DataStoreVoice>
        get() = datastore.SelectedVoice()
            .value
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                DataStoreManager.DataStoreVoice()
            )


    val colors =  listOf(
        Color(0xFF00B0FF), Color(0xFF00E5FF), Color(0xFF1DE9B6),
        Color(0xFF76FF03), Color(0xFFFFEA00), Color(0xFFFF9100),
        Color(0xFFFF1744), Color(0xFFD500F9), Color(0xFF6200EA)
    )

    init {
        observeVoices()
    }

    private fun observeVoices() {
        repository.getVoices()
            .onEach(_voices::emit)
            .launchIn(viewModelScope)
    }

    fun updateCurrentVoice(voice: Voice) {
        playVoiceSample(voice.previewUrl)
        viewModelScope.launch(Dispatchers.IO) {
            datastore.SelectedVoice().update(
                id = voice.voiceId,
                name = voice.name
            )
        }
    }

    private fun playVoiceSample(
        previewUrl: String
    ) {
        if (currentPlayingVoice == previewUrl) {
            mediaPlayer.stop()
            currentPlayingVoice = null
        } else {
            mediaPlayer.setup(previewUrl)
            currentPlayingVoice = previewUrl
        }
    }

    fun stopMedia() {
        mediaPlayer.stop()
    }

    fun clearMediaPlayer() {
        mediaPlayer.release()
    }
}