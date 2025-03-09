package dev.mathroda.twelvereader.ui.screens.writetext

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.repository.Repository
import dev.mathroda.twelvereader.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface WriteScreenActions {
    data class NavigateToMainPlayer(val uri: String, val text: String): WriteScreenActions
    data class ShowToastMessage(val message: String): WriteScreenActions
}

class WriteTextViewModel(
    private val repository: Repository,
    private val dataStore: DataStoreManager,
    private val context: Application
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uiActions = MutableSharedFlow<WriteScreenActions>(replay = 0)
    val uiActions = _uiActions.asSharedFlow()

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
                            _uiActions.emit(WriteScreenActions.NavigateToMainPlayer(file.path, text))
                        }
                        is Resource.Error -> {
                            updateIsLoading(false)
                            _uiActions.emit(WriteScreenActions.ShowToastMessage(result.message))
                        }

                    }
                }
        }
    }

    private fun updateIsLoading(loading: Boolean)  {
        _isLoading.update { loading }
    }
}