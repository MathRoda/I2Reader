@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.mathroda.twelvereader.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.domain.Voice
import dev.mathroda.twelvereader.infrastructure.mediaplayer.MyMediaPlayer
import dev.mathroda.twelvereader.repository.Repository
import dev.mathroda.twelvereader.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class HomeViewModel(
    private val datastore: DataStoreManager,
    private val repository: Repository,
    private val mediaPlayer: MyMediaPlayer
): ViewModel() {

    private var currentPlayingVoice: String? = null

    var currentVoiceState = mutableStateOf("")
        private set

    val selectedVoice: StateFlow<Resource<Voice>>
        get() = datastore.SelectedVoice().value.flatMapLatest { voiceId ->
            currentVoiceState.value = voiceId
            delay(500)
            repository.getVoiceById(voiceId)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            Resource.Loading()
        )

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

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }


}