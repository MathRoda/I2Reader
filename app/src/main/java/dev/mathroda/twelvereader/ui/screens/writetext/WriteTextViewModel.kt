package dev.mathroda.twelvereader.ui.screens.writetext

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.infrastructure.mediaplayer.MyMediaPlayer
import dev.mathroda.twelvereader.repository.Repository
import dev.mathroda.twelvereader.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WriteTextViewModel(
    private val mediaPlayer: MyMediaPlayer,
    private val repository: Repository,
    private val dataStore: DataStoreManager,
    private val context: Application
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun textToSpeech(
        text: String
    ) {
        viewModelScope.launch {
            val voiceId = dataStore.SelectedVoice().value.first()
            repository.textToSpeech(text, voiceId)
                .collectLatest { result ->
                    when(result) {
                        is Resource.Loading -> updateIsLoading(true)
                        is Resource.Success -> {
                            updateIsLoading(false)
                            val file = result.data.createMp3File(context)
                            mediaPlayer.setupFile(file)
                        }
                        is Resource.Error -> Unit
                    }
                }
        }
    }

    private fun updateIsLoading(loading: Boolean)  {
        _isLoading.update { loading }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }

}