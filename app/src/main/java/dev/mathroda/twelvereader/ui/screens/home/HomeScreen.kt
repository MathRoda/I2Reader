package dev.mathroda.twelvereader.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mathroda.twelvereader.ui.screens.common.CommonTopBar
import dev.mathroda.twelvereader.ui.screens.common.CommonVoiceCard
import dev.mathroda.twelvereader.ui.screens.common.CommonVoiceCardShimmer
import dev.mathroda.twelvereader.ui.screens.common.CommonVoiceRow
import dev.mathroda.twelvereader.ui.screens.common.CommonVoiceRowShimmer
import dev.mathroda.twelvereader.utils.Resource
import org.koin.compose.viewmodel.koinViewModel

@ExperimentalMaterial3Api
@Composable
fun HomeScreen() {

    val viewModel: HomeViewModel = koinViewModel()
    val selectedVoice by viewModel.selectedVoice.collectAsStateWithLifecycle()
    val library by viewModel.voices.collectAsStateWithLifecycle()
    val currentVoiceState by viewModel.currentVoiceState

    Scaffold(
        topBar = {
            CommonTopBar(
                title = { Text(text = "Voices", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                icon = { Icon(imageVector = Icons.Default.FilterVintage, contentDescription = null) },
                onAction = { }
            )
        }
    ) { paddingValues ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    when(val voice = selectedVoice) {
                        is Resource.Loading-> CommonVoiceCardShimmer()
                        is Resource.Success -> {
                            CommonVoiceCard(voice = voice.data) {
                                viewModel.playVoiceSample(it.previewUrl)
                            }
                        }
                        is Resource.Error -> Unit
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                Column (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Different voices to choose from",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.4f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Explore our library",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            when(val result = library) {
                is Resource.Loading -> items(10) {
                    CommonVoiceRowShimmer()
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = DividerDefaults.color.copy(0.3f)
                    )
                }
                is Resource.Success -> items(result.data) { voice ->
                    CommonVoiceRow(
                        voice = voice,
                        isSelected = currentVoiceState == voice.voiceId,
                        onPreview = viewModel::playVoiceSample,
                        onFavoriteVoice = { viewModel.updateSelectedVoice(it.voiceId) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = DividerDefaults.color.copy(0.3f)
                    )
                }
                is Resource.Error -> Unit
            }
        }
    }
}