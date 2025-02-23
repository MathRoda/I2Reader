package dev.mathroda.twelvereader.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.domain.Voice
import dev.mathroda.twelvereader.infrastructure.mediaplayer.MyMediaPlayer
import dev.mathroda.twelvereader.repository.Repository
import dev.mathroda.twelvereader.ui.di.viewModelModule
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