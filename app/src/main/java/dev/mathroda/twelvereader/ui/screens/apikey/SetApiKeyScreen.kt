package dev.mathroda.twelvereader.ui.screens.apikey

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@ExperimentalMaterial3Api
@Composable
fun SetApiKeyScreen() {
    val viewModel: SetApiKeyViewModel = koinViewModel()
    val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()
    val savedApiKey by viewModel.savedApiKey.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(viewModel) {
        viewModel.uiAction.collectLatest { action ->
            when (action) {
                is SetApiKeyActions.SaveKey -> viewModel.saveApiKey()
                is SetApiKeyActions.PasteClipboard -> {
                    clipboardManager.getText()?.text
                        ?.let { text -> viewModel.updateApiKey(text) }
                }
                is SetApiKeyActions.OpenUri -> uriHandler.openUri(action.uri)
                else -> Unit
            }
        }
    }

    LaunchedEffect(savedApiKey) {
        if (savedApiKey.isNotEmpty()) {
            viewModel.updateApiKey(savedApiKey)
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "XII",
                    style = MaterialTheme.typography.displayLarge,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "I'm too broke to let you use my API key!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Add your own key to continue using the app.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = viewModel::updateApiKey,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("API Key") },
                    placeholder = { Text("Enter your API key here") },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.sendUiAction(SetApiKeyActions.PasteClipboard) }
                        ) {
                            Icon(
                                Icons.Default.ContentPaste,
                                contentDescription = "Paste from clipboard"
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )

                Spacer(modifier = Modifier.height(16.dp))

                val getApiKeyText = buildAnnotatedString {
                    append("Don't have an API key? Get one ")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("here")
                    }
                }

                Text(
                    text = getApiKeyText,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { viewModel.sendUiAction(SetApiKeyActions.OpenUri("https://elevenlabs.io/account")) },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(visible = apiKey.isNotBlank() && apiKey != savedApiKey) {
                    Text(
                        text = "Don't forget to save your API key!",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.sendUiAction(SetApiKeyActions.SaveKey) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = apiKey.isNotBlank() && apiKey != savedApiKey,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Store key")
                }
            }
        }
    }
}