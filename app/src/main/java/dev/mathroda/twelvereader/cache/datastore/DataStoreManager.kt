package dev.mathroda.twelvereader.cache.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

const val dataStoreName = "Preferences"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = dataStoreName)

class DataStoreManager(
    private val context: Context
) {
    private val SELECTED_VOICE_KEY = stringPreferencesKey("selected_voice")

    inner class SelectedVoice {
        val value: Flow<String>
            get() = context.dataStore.data.map {
                it[SELECTED_VOICE_KEY] ?: ""
            }

        suspend fun update(value: String) {
            withContext(Dispatchers.IO) {
                context.dataStore.edit {
                    it[SELECTED_VOICE_KEY] = value
                }
            }
        }
    }

}