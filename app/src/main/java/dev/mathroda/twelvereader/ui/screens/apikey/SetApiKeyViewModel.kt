package dev.mathroda.twelvereader.ui.screens.apikey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.ui.navigation.Destination
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SetApiKeyActions {
    data class NavigateToOnboarding(val destination: Destination): SetApiKeyActions
    data object PasteClipboard: SetApiKeyActions
    data object SaveKey: SetApiKeyActions
    data class OpenUri(val uri: String): SetApiKeyActions
}

class SetApiKeyViewModel(
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    private val _apiKey = MutableStateFlow("")
    val apiKey = _apiKey.asStateFlow()

    private val _uiAction = MutableSharedFlow<SetApiKeyActions>(replay = 0)
    val uiAction = _uiAction.asSharedFlow()

    val savedApiKey = dataStoreManager.ElevenLabsApiKey().value
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun updateApiKey(key: String) {
        _apiKey.update { key }
    }

    fun sendUiAction(
        action: SetApiKeyActions
    ) {
        viewModelScope.launch {
            _uiAction.emit(action)
        }
    }

    fun saveApiKey() {
        viewModelScope.launch {
            dataStoreManager.ElevenLabsApiKey().update(apiKey.value)
        }
    }
}