package dev.mathroda.twelvereader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.ui.navigation.Destination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    dataStore: DataStoreManager
): ViewModel() {

    private val selectedVoice = dataStore.SelectedVoice().value
    private val apiKey = dataStore.ElevenLabsApiKey().value

    val startDestination: StateFlow<Destination>
        get() = selectedVoice.combine(apiKey) { selectedVoice, apiKey ->
            selectedVoice.id to apiKey
        }.map { (selectedVoiceId, apiKey) ->
            when {
                apiKey.isEmpty() -> Destination.SetApiKey
                selectedVoiceId.isEmpty() -> Destination.Onboarding
                else -> Destination.Home
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = Destination.Home
        )


}