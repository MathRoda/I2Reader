package dev.mathroda.twelvereader.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mathroda.twelvereader.domain.Voice
import dev.mathroda.twelvereader.ui.common.CommonCircleLoading
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
            VoiceCard(
                voice = voice,
                isSelected = voice == selectedVoice,
                updateChosenVoice = updateChosenVoice
            )
        }
    }
}

@Composable
fun VoiceCard(
    voice: Voice,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    updateChosenVoice: (Voice) -> Unit
) {
    val name = remember(voice.name) {
        voice.name.split(" ")[0]
    }

    val age = remember(voice.age) {
        voice.age.replace("_", "-")
    }

    Card(
        modifier = modifier,
        onClick = { updateChosenVoice(voice) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 0.dp
        ),
        border = if (isSelected) BorderStroke(2.dp, Color.Black) else BorderStroke(1.dp, Color.LightGray),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AbstractCircleArt()

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "$age ${voice.accent} ${voice.gender}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun AbstractCircleArt(
    modifier: Modifier = Modifier.size(48.dp)
) {
    val colors = remember {
        listOf(
            Color(0xFF00B0FF), Color(0xFF00E5FF), Color(0xFF1DE9B6),
            Color(0xFF76FF03), Color(0xFFFFEA00), Color(0xFFFF9100),
            Color(0xFFFF1744), Color(0xFFD500F9), Color(0xFF6200EA)
        )
    }

    val randomGradient = remember {
        Brush.sweepGradient(
            colors = colors.shuffled(),
            center = Offset(100f, 100f)
        )
    }

    Canvas(
        modifier = modifier.clip(CircleShape)
    ) {
        // Background smooth gradient
        drawCircle(
            brush = randomGradient,
            radius = size.minDimension / 2f,
            center = center
        )

        // Add distortions using path
        val path = Path().apply {
            moveTo(center.x, center.y)
            cubicTo(size.width * 0.3f, size.height * 0.1f, size.width * 0.7f, size.height * 0.9f, center.x, center.y)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.radialGradient(
                colors = colors.shuffled(),
                center = center,
                radius = size.minDimension * 0.5f
            ),
            style = Fill
        )
    }
}


