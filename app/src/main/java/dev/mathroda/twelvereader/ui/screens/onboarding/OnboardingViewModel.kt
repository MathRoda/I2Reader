package dev.mathroda.twelvereader.ui.screens.onboarding

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.domain.Voice
import dev.mathroda.twelvereader.infrastructure.mediaplayer.MyMediaPlayer
import dev.mathroda.twelvereader.repository.Repository
import dev.mathroda.twelvereader.utils.Resource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class OnboardingViewModel(
    private val repository: Repository,
    private val datastore: DataStoreManager,
    private val mediaPlayer: MyMediaPlayer
): ViewModel() {

    private var currentPlayingVoice: Voice? = null

    val colors =  listOf(
        Color(0xFF00B0FF), Color(0xFF00E5FF), Color(0xFF1DE9B6),
        Color(0xFF76FF03), Color(0xFFFFEA00), Color(0xFFFF9100),
        Color(0xFFFF1744), Color(0xFFD500F9), Color(0xFF6200EA)
    ).shuffled()

    val voices: StateFlow<Resource<List<Voice>>>
        get() = repository.getVoices()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                Resource.Loading()
            )

    fun updateSelectedVoice(value: String) {
        viewModelScope.launch {
            datastore.SelectedVoice().update(value)
        }
    }

    fun playVoiceSample(
        voice: Voice
    ) {
        if (currentPlayingVoice == voice) {
            mediaPlayer.stop()
            currentPlayingVoice = null
        } else {
            mediaPlayer.setup(voice.previewUrl)
            currentPlayingVoice = voice
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }

}