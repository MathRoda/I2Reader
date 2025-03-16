package dev.mathroda.twelvereader.ui.screens.writetext

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mathroda.twelvereader.ui.screens.mainplayer.MainPlayerViewModel

@ExperimentalMaterial3Api
@Composable
fun WriteTextScreen(
    navigateBack: () -> Unit,
    viewModel: MainPlayerViewModel,
    navigateToMainPlayer: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val spokenText by viewModel.spokenText.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
           TopAppBar(
               title = { Text("Write anything", fontWeight = FontWeight.Bold) },
               navigationIcon = {
                   IconButton(onClick = navigateBack) {
                      Icon(
                          imageVector = Icons.Default.ArrowBackIosNew,
                          contentDescription = null
                      )
                   }
               },
               actions = {
                   Button(
                       modifier = Modifier.padding(8.dp),
                       onClick = {
                           viewModel.generateAudio(spokenText.text)
                           navigateToMainPlayer()
                       },
                       enabled = (spokenText.text.isNotEmpty() || spokenText.text.isNotBlank()) && !isLoading,
                       colors = ButtonDefaults.buttonColors(
                           containerColor = MaterialTheme.colorScheme.primary,
                           contentColor = MaterialTheme.colorScheme.onPrimary,
                       )
                   ) {
                       Icon(
                           imageVector = Icons.Default.Headset,
                           contentDescription = null
                       )

                       Spacer(Modifier.width(4.dp))
                       Text("Listen")
                   }
               }
           )
        }
    ) { paddingValues ->

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = spokenText.text,
                onValueChange = viewModel::updateText,
                textStyle = MaterialTheme.typography.bodyLarge
            )

            if(spokenText.text.isEmpty()) {
                Text(
                    text = "Paste or write text to listen",
                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                )
            }
        }
    }
}