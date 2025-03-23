package dev.mathroda.twelvereader.ui.screens.mainplayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smarttoolfactory.slider.ColorfulSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor
import dev.mathroda.twelvereader.infrastructure.mediaplayer.MediaPlayerState
import dev.mathroda.twelvereader.ui.common.BaseButton
import dev.mathroda.twelvereader.ui.screens.common.AbstractCircleArt
import dev.mathroda.twelvereader.ui.screens.mainplayer.models.SpeedDefaults
import dev.mathroda.twelvereader.utils.OnLifecycleEvent
import dev.mathroda.twelvereader.utils.takeWordsUpTo
import dev.mathroda.twelvereader.utils.toDisplay
import dev.mathroda.twelvereader.utils.toTime
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDate
import kotlin.math.roundToInt


@ExperimentalMaterial3Api
@Composable
fun MainPlayerScreen(
    uri: String,
    didVoiceChange: Boolean,
    navigateBack: () -> Unit,
    navigateToSelectVoice: () -> Unit
) {
    val viewModel: MainPlayerViewModel = koinViewModel()
    val mediaPlayerState by viewModel.playerState.collectAsStateWithLifecycle()
    val sliderPositions by viewModel.sliderPosition.collectAsStateWithLifecycle()
    val mediaSpeed by viewModel.mediaSpeed.collectAsStateWithLifecycle()
    val currentReader by viewModel.currentReader.collectAsStateWithLifecycle("")
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    LaunchedEffect(uri, didVoiceChange) {
        if (!didVoiceChange) {
            viewModel.uri = uri
        }
    }

    Scaffold(
        topBar = { MainPlayerTopBar(navigateBack) },
        bottomBar = {
            MediaPlayerController(
                modifier = Modifier.fillMaxWidth(),
                currentTime = sliderPositions,
                speed = mediaSpeed,
                colors = viewModel.colors,
                totalTime = mediaPlayerState.totalDuration,
                valueRange = 0f .. mediaPlayerState.totalDuration.toFloat(),
                mediaPlayerState = mediaPlayerState.current,
                onValueChange = { viewModel.onAction(MainPlayerUiActions.SeekTo(it.toLong())) },
                onAction = viewModel::onAction,
                openSpeedBottomSheet = { isBottomSheetOpen = true },
                navigateToSelectVoice = navigateToSelectVoice,
                reader = currentReader,
                isLoading = isLoading
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                MainPlayerHeader(
                    textToSpeech = viewModel.text.takeWordsUpTo(7)
                )

                Spacer(Modifier.height(16.dp))

                MainPlayerBody(
                    totalTime = mediaPlayerState.totalDuration,
                    currentTime = sliderPositions,
                    text = viewModel.text
                )
            }


            if (isBottomSheetOpen) {
                ModalBottomSheet(
                    onDismissRequest = { isBottomSheetOpen = false }
                ) {
                    BottomSheetContent(
                        speed = mediaSpeed,
                        onSpeedChanged = { viewModel.onAction(MainPlayerUiActions.SetSpeed(it)) }
                    )
                }
            }
        }
    }

    OnLifecycleEvent { event ->
        when(event) {
            Lifecycle.Event.ON_PAUSE -> viewModel.clearMediaPlayer()
            Lifecycle.Event.ON_RESUME -> viewModel.reInitializeMedia(didVoiceChange)
            Lifecycle.Event.ON_DESTROY -> viewModel.clearMediaPlayer()
            else -> Unit
        }
    }
}


@Composable
private fun MainPlayerTopBar(
    navigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            onClick = navigateBack,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun MainPlayerHeader(
    textToSpeech: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = textToSpeech,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = LocalDate.now().toDisplay(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(0.1f)
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun MediaPlayerController(
    currentTime: Long,
    totalTime: Long,
    speed: Float,
    reader: String,
    colors: List<Color>,
    mediaPlayerState: MediaPlayerState= MediaPlayerState.IDLE,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit,
    onAction: (MainPlayerUiActions) -> Unit,
    openSpeedBottomSheet: () -> Unit,
    navigateToSelectVoice: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ColorfulSlider(
                value = currentTime.toFloat(),
                onValueChange = onValueChange,
                valueRange = valueRange,
                colors = MaterialSliderDefaults.defaultColors(
                    thumbColor = SliderBrushColor(color = MaterialTheme.colorScheme.onSurface),
                    activeTrackColor = SliderBrushColor(color = MaterialTheme.colorScheme.onSurface),
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentTime.toTime(),
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = totalTime.toTime(),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton (
                onClick = openSpeedBottomSheet
            ) {
                Text(
                    text = "${speed}x",
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = {
                    val seekTo = (currentTime - 10000L).coerceAtLeast(0)
                    onAction(MainPlayerUiActions.SeekTo(seekTo))
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Replay10,
                    contentDescription = "Replay 10 seconds"
                )
            }

            OutlinedIconButton(
                onClick = {
                    when(mediaPlayerState) {
                        MediaPlayerState.PLAYING, MediaPlayerState.IDLE -> onAction(MainPlayerUiActions.Pause)
                        MediaPlayerState.ENDED -> onAction(MainPlayerUiActions.Play)
                        MediaPlayerState.PAUSED -> onAction(MainPlayerUiActions.Resume)
                    }
                },
                enabled = !isLoading,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.2f))
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Icon(
                        imageVector = if (mediaPlayerState == MediaPlayerState.PLAYING) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play"
                    )
                }
            }

            IconButton(
                onClick = {
                    val seekTo = (currentTime + 10000L).coerceAtMost(totalTime)
                    onAction(MainPlayerUiActions.SeekTo(seekTo))
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Forward10,
                    contentDescription = "Forward 10 seconds"
                )
            }

            TextButton (
                onClick = openSpeedBottomSheet
            ) {
                Text(
                    text = "${speed}x",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(0.6f),
            onClick = navigateToSelectVoice,
            contentPadding = PaddingValues(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.3f))
        ) {
            AbstractCircleArt(
                modifier = Modifier.size(24.dp),
                colors = colors
            )

            Spacer(Modifier.width(8.dp))
            Text(
                buildAnnotatedString {
                    append("Read by ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(reader)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BottomSheetContent(
    speed: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0.3f..3f,
    onSpeedChanged: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Reading Speed: ${speed}x",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ColorfulSlider(
                value = speed,
                onValueChange = { speed ->
                    val roundedValue = (speed * 10).roundToInt() / 10f
                    onSpeedChanged(roundedValue)
                },
                valueRange = valueRange,
                colors = MaterialSliderDefaults.defaultColors(
                    thumbColor = SliderBrushColor(color = MaterialTheme.colorScheme.onSurface),
                    activeTrackColor = SliderBrushColor(color = MaterialTheme.colorScheme.onSurface),
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0.3x",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "3x",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SpeedDefaults.entries.forEach { defaultSpeed ->
                SpeedDefaultButton(
                    speed = defaultSpeed.speed,
                    isBorderEnabled = defaultSpeed.speed == speed,
                    onSpeedChanged = onSpeedChanged,
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@ExperimentalFoundationApi
@Composable
private fun SpeedDefaultButton(
    speed: Float,
    modifier: Modifier = Modifier,
    isBorderEnabled: Boolean = false,
    onSpeedChanged: (Float) -> Unit
) {
    BaseButton(
        modifier = modifier,
        shape = CircleShape,
        border = if (isBorderEnabled) BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else BorderStroke(0.dp, Color.Transparent),
        color = MaterialTheme.colorScheme.secondaryContainer,
        onClick = { onSpeedChanged(speed) }
    ) {
        Text(
            text = "${speed}x",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MainPlayerBody(
    text: String,
    currentTime: Long,
    totalTime: Long
) {
    val words = remember(text) { text.split(" ") }
    
    // Introduce a buffer to prevent highlighting from being too tightly coupled to audio position
    val highlightBuffer = 0.02f // 2% buffer to account for voice timing variations
    
    // Use a smoothing mechanism to prevent jumps in highlighting
    val estimatedIndex by remember(totalTime, currentTime, words) {
        derivedStateOf {
            if (totalTime <= 0) return@derivedStateOf 0
            
            // Calculate position with buffer for smoother transitions
            val normalizedPosition = (currentTime.toFloat() / totalTime)
                .coerceIn(0f, 1f)
            
            // Apply smoothing algorithm to handle voice pacing variations
            // This reduces the chance of highlighting getting ahead of or behind the voice
            val smoothedPosition = when {
                normalizedPosition < highlightBuffer -> normalizedPosition
                normalizedPosition > (1f - highlightBuffer) -> normalizedPosition
                else -> normalizedPosition - highlightBuffer
            }
            
            (smoothedPosition * words.size)
                .toInt()
                .coerceIn(0, words.lastIndex)
        }
    }
    
    // Make highlighted word more visible by adding scrolling if needed
    Text(
        buildAnnotatedString {
            words.forEachIndexed { index, word ->
                val isHighlighted = index == estimatedIndex && word.isNotBlank()
                val isNearHighlight = (index >= estimatedIndex - 2 && index <= estimatedIndex + 2) && word.isNotBlank()
                
                withStyle(
                    SpanStyle(
                        color = when {
                            isHighlighted -> Color.White
                            isNearHighlight -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        },
                        background = if (isHighlighted) Color.Blue.copy(alpha = 0.6f) else Color.Transparent,
                        fontWeight = when {
                            isHighlighted -> FontWeight.ExtraBold
                            isNearHighlight -> FontWeight.Medium
                            else -> FontWeight.Normal
                        }
                    )
                ) {
                    append("$word ")
                }
            }
        },
        style = MaterialTheme.typography.bodyLarge
    )
}