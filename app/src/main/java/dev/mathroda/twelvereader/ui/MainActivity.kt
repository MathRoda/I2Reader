package dev.mathroda.twelvereader.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dev.mathroda.twelvereader.ui.navigation.MainGraph
import dev.mathroda.twelvereader.ui.theme.TwelveReaderTheme
import org.koin.compose.viewmodel.koinViewModel

@ExperimentalLayoutApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = koinViewModel()
            val selectedVoice by viewModel.selectedVoice.collectAsStateWithLifecycle()
            val navHostController = rememberNavController()

            TwelveReaderTheme(dynamicColor = false) {
                Box(Modifier.safeDrawingPadding()) {
                    MainGraph(
                        navController = navHostController,
                        startDestination = selectedVoice
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TwelveReaderTheme {
        Greeting("Android")
    }
}