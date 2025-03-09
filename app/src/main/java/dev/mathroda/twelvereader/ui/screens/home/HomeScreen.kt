package dev.mathroda.twelvereader.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mathroda.twelvereader.ui.common.BaseButton
import dev.mathroda.twelvereader.ui.screens.common.CommonTopBar
import dev.mathroda.twelvereader.ui.screens.common.CommonVoiceCard
import dev.mathroda.twelvereader.ui.screens.common.CommonVoiceCardShimmer
import dev.mathroda.twelvereader.ui.screens.common.CommonVoiceRow
import dev.mathroda.twelvereader.ui.screens.common.CommonVoiceRowShimmer
import dev.mathroda.twelvereader.utils.Resource
import org.koin.compose.viewmodel.koinViewModel

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navigateToWriteText: () -> Unit
) {

    val viewModel: HomeViewModel = koinViewModel()
    val selectedVoice by viewModel.selectedVoice.collectAsStateWithLifecycle()
    val library by viewModel.voices.collectAsStateWithLifecycle()
    val currentVoiceState by viewModel.currentVoiceState
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearMediaPlayer()
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = { Text(text = "Voices", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                onAction = { isBottomSheetOpen = true }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        when(val voice = selectedVoice) {
                            is Resource.Loading-> CommonVoiceCardShimmer()
                            is Resource.Success -> {
                                CommonVoiceCard(
                                    voice = voice.data,
                                    colors = viewModel.colors
                                ) {
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
                            colors = viewModel.colors,
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

            if (isBottomSheetOpen) {
                ModalBottomSheet(
                    onDismissRequest = { isBottomSheetOpen = false }
                ) {
                    BottomSheetContent(
                        dismissBottomSheet = { isBottomSheetOpen = false },
                        navigateToWriteText = navigateToWriteText
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalLayoutApi
@Composable
private fun BottomSheetContent(
    dismissBottomSheet: () -> Unit,
    navigateToWriteText: () -> Unit,
) {
   val importButtons = listOf(
       Icons.Default.Link to "Paste Link",
       Icons.Default.ImportExport to "Write Text",
       Icons.Default.FilePresent to "Import File",
       Icons.Outlined.CameraAlt to "Scan",
   )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Import",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            importButtons.forEach { (icon, title) ->
                ImportButton(
                    icon = { Icon(imageVector = icon, contentDescription = null) },
                    title = title,
                ) {
                    dismissBottomSheet()
                    when(title) {
                        "Paste Link" -> {}
                        "Write Text" -> navigateToWriteText()
                        "Import File" -> {}
                        "Scan" -> {}
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun ImportButton(
    icon: @Composable () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BaseButton(
            modifier = modifier,
            onClick = onClick,
            enabled = title == "Write Text",
            border = null
        ) {
           icon()
        }

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
        )
    }
}
