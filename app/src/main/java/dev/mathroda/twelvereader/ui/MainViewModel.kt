package dev.mathroda.twelvereader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mathroda.twelvereader.cache.datastore.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val dataStore: DataStoreManager
): ViewModel() {

    val selectedVoice: StateFlow<String>
        get() = dataStore.SelectedVoice().value
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                ""
            )
}