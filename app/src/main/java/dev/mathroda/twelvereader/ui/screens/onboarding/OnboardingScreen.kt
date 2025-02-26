package dev.mathroda.twelvereader.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mathroda.twelvereader.domain.Voice
import dev.mathroda.twelvereader.ui.common.CommonCircleLoading
import dev.mathroda.twelvereader.ui.screens.common.CommonVoiceCard
import dev.mathroda.twelvereader.utils.Resource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingScreen() {

    val viewModel: OnboardingViewModel = koinViewModel()
    val state by viewModel.voices.collectAsStateWithLifecycle()
    var selectedVoice by remember { mutableStateOf<Voice?>(null) }

    Scaffold(
        bottomBar = {
            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                enabled = selectedVoice != null,
                onClick = {
                    selectedVoice?.let {
                        viewModel.updateSelectedVoice(it.voiceId)
                    }
                }
            ) { Text("Continue") }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "The fun part starts. Let's pick a voice to start with!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "You can explore our library with hundreds of other voice options and change this later too.",
                style = MaterialTheme.typography.bodyLarge
            )

            when(val result = state){
                is Resource.Loading -> CommonCircleLoading(true)
                is Resource.Success -> VoicesList(
                    selectedVoice = selectedVoice,
                    voices = { result.data },
                    updateChosenVoice = { voice ->
                        selectedVoice = voice
                        viewModel.playVoiceSample(voice)
                    }
                )
                is Resource.Error -> {}
            }
        }
    }
}

@Composable
fun VoicesList(
    selectedVoice: Voice?,
    voices: () -> List<Voice>,
    updateChosenVoice: (Voice) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(voices()) { voice ->
            CommonVoiceCard(
                voice = voice,
                isSelected = voice == selectedVoice,
                updateChosenVoice = updateChosenVoice
            )
        }
    }
}

