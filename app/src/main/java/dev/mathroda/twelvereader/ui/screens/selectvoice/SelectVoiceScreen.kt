package dev.mathroda.twelvereader.ui.screens.selectvoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mathroda.twelvereader.ui.screens.common.CommonTopBar
import dev.mathroda.twelvereader.ui.screens.common.CommonVoiceRow
import dev.mathroda.twelvereader.ui.screens.common.CommonVoiceRowShimmer
import dev.mathroda.twelvereader.utils.Resource
import org.koin.compose.viewmodel.koinViewModel

@ExperimentalMaterial3Api
@Composable
fun SelectVoiceScreen(
    navigateBack: (didVoiceChange: Boolean) -> Unit,
) {
    val viewModel: SelectVoiceViewModel = koinViewModel()
    val library by viewModel.voices.collectAsStateWithLifecycle()
    val selectVoice by viewModel.selectedVoice.collectAsStateWithLifecycle()
    val defaultVoiceId by remember { mutableStateOf(selectVoice.id) }

    DisposableEffect(Unit) {
        onDispose { viewModel.clearMediaPlayer() }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = { Text(text = "Select a voice", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                icon = { Icon(imageVector = Icons.Default.Close, contentDescription = null) },
                onAction = {
                    viewModel.stopMedia()
                    val didVoiceChange = defaultVoiceId != selectVoice.id
                    navigateBack(didVoiceChange)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                item {
                    Text(
                        text = "Recommended for you ...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(16.dp))
                }

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
                            colors = viewModel.colors,
                            isSelected = selectVoice.id == voice.voiceId,
                            onPreview = {},
                            isPreviewEnabled = false,
                            onFavoriteVoice = viewModel::updateCurrentVoice
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = DividerDefaults.color.copy(0.3f)
                        )
                    }
                    is Resource.Error -> Unit
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // Height of the fading effect
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0f), Color.White),
                            startY = 0f,
                            endY = 100f
                        )
                    )
                    .align(Alignment.BottomStart) // Align fade at the bottom
            )
        }

    }
}

@Composable
private fun BottomContent(
    onReset: () -> Unit,
    onSave: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f),
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset", fontWeight = FontWeight.Bold)
            }

            Button(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f),
                onClick = onSave,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        }
    }

}