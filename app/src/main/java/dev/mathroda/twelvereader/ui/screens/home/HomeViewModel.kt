@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.mathroda.twelvereader.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.domain.Voice
import dev.mathroda.twelvereader.infrastructure.mediaplayer.MyMediaPlayer
import dev.mathroda.twelvereader.repository.Repository
import dev.mathroda.twelvereader.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class HomeViewModel(
    private val datastore: DataStoreManager,
    private val repository: Repository,
    private val mediaPlayer: MyMediaPlayer
): ViewModel() {

    private var currentPlayingVoice: String? = null

    var currentVoiceState = mutableStateOf("")
        private set

    private val _selectedVoice = MutableStateFlow<Resource<Voice>>(Resource.Loading())
    val selectedVoice = _selectedVoice.asStateFlow()

    private val _voices = MutableStateFlow<Resource<List<Voice>>>(Resource.Loading())
    val voices = _voices.asStateFlow()

    val colors =  listOf(
        Color(0xFF00B0FF), Color(0xFF00E5FF), Color(0xFF1DE9B6),
        Color(0xFF76FF03), Color(0xFFFFEA00), Color(0xFFFF9100),
        Color(0xFFFF1744), Color(0xFFD500F9), Color(0xFF6200EA)
    )

    init {
        observeSelectedVoice()
        observeVoices()
    }

    private fun observeSelectedVoice() {
        datastore.SelectedVoice().value
            .flatMapLatest { voiceId ->
                currentVoiceState.value = voiceId
                delay(500)
                repository.getVoiceById(voiceId)
            }.onEach(_selectedVoice::emit)
            .launchIn(viewModelScope)
    }

    private fun observeVoices() {
        repository.getVoices()
            .onEach(_voices::emit)
            .launchIn(viewModelScope)
    }

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
            mediaPlayer.setupUrl(previewUrl)
            currentPlayingVoice = previewUrl
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }

}