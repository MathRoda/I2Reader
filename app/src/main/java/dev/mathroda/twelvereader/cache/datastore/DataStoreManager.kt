package dev.mathroda.twelvereader.cache.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
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
    private val MEDIA_SPEED_DEFAULT_KEY = floatPreferencesKey("media_speed_default")

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

    inner class MediaSpeedDefault {
        val value: Flow<Float>
            get() = context.dataStore.data.map {
                it[MEDIA_SPEED_DEFAULT_KEY] ?: 1f
            }

        suspend fun update(value: Float) {
            withContext(Dispatchers.IO) {
                context.dataStore.edit {
                    it[MEDIA_SPEED_DEFAULT_KEY] = value
                }
            }
        }
    }

}