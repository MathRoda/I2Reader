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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smarttoolfactory.slider.ColorfulSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor
import dev.mathroda.twelvereader.infrastructure.mediaplayer.MediaPlayerState
import dev.mathroda.twelvereader.ui.common.BaseButton
import dev.mathroda.twelvereader.ui.screens.common.AbstractCircleArt
import dev.mathroda.twelvereader.ui.screens.mainplayer.models.SpeedDefaults
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
    text: String,
    navigateBack: () -> Unit
) {
    val viewModel: MainPlayerViewModel = koinViewModel()
    val mediaPlayerState by viewModel.playerState.collectAsStateWithLifecycle()
    val sliderPositions by viewModel.sliderPosition.collectAsStateWithLifecycle()
    val mediaSpeed by viewModel.mediaSpeed.collectAsStateWithLifecycle()
    var textToSpeech by remember { mutableStateOf("") }
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        viewModel.initializeMedia(uri)
        textToSpeech = text
        onDispose { viewModel.clearMediaPlayer() }
    }

    Scaffold(
        topBar = { MainPlayerTopBar(navigateBack) },
        bottomBar = {
            MediaPlayerController(
                modifier = Modifier.fillMaxWidth(),
                uri = uri,
                currentTime = sliderPositions,
                speed = mediaSpeed,
                colors = viewModel.colors,
                totalTime = mediaPlayerState.totalDuration,
                valueRange = 0f .. mediaPlayerState.totalDuration.toFloat(),
                mediaPlayerState = mediaPlayerState.current,
                onValueChange = { viewModel.onAction(MainPlayerUiActions.SeekTo(it.toLong())) },
                onAction = viewModel::onAction,
                openSpeedBottomSheet = { isBottomSheetOpen = true }
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
                    textToSpeech = textToSpeech.takeWordsUpTo(7)
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
    uri: String,
    currentTime: Long,
    totalTime: Long,
    speed: Float,
    colors: List<Color>,
    mediaPlayerState: MediaPlayerState= MediaPlayerState.IDLE,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit,
    onAction: (MainPlayerUiActions) -> Unit,
    openSpeedBottomSheet: () -> Unit
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
                        MediaPlayerState.ENDED -> onAction(MainPlayerUiActions.Play(uri))
                        MediaPlayerState.PAUSED -> onAction(MainPlayerUiActions.Resume)
                    }
                },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.2f))
            ) {
                Icon(
                    imageVector = if (mediaPlayerState == MediaPlayerState.PLAYING) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "Play"
                )
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
            onClick = {},
            contentPadding = PaddingValues(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.3f))
        ) {
            AbstractCircleArt(
                modifier = Modifier.size(24.dp),
                colors = colors
            )

            Spacer(Modifier.width(8.dp))
            Text("Read by Thomas")
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