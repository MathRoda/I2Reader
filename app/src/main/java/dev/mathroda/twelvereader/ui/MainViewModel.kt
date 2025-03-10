package dev.mathroda.twelvereader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import dev.mathroda.twelvereader.ui.navigation.Destination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val dataStore: DataStoreManager
): ViewModel() {

    val selectedVoice: StateFlow<Destination>
        get() = dataStore.SelectedVoice().value
            .map { voice ->
                if (voice.id.isEmpty()) Destination.Onboarding else Destination.Home
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                Destination.Home
            )
}