package dev.mathroda.twelvereader.ui.screens.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@ExperimentalMaterial3Api
@Composable
fun CommonTopBar(
    title: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onAction: () -> Unit
) {
    TopAppBar(
        title = title,
        actions = {
            FilledIconButton(
                onClick = onAction,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.LightGray.copy(0.4f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                icon()
            }
        }
    )
}