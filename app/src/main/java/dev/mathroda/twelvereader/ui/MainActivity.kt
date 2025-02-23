package dev.mathroda.twelvereader.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import dev.mathroda.twelvereader.network.NetworkService
import dev.mathroda.twelvereader.repository.Repository
import dev.mathroda.twelvereader.ui.navigation.Destination
import dev.mathroda.twelvereader.ui.navigation.MainGraph
import dev.mathroda.twelvereader.ui.screens.onboarding.OnboardingScreen
import dev.mathroda.twelvereader.ui.theme.TwelveReaderTheme
import dev.mathroda.twelvereader.utils.Resource
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navHostController = rememberNavController()
            val viewModel: MainViewModel = koinViewModel()
            val selectedVoice by viewModel.selectedVoice.collectAsStateWithLifecycle()

            TwelveReaderTheme {
                Box(Modifier.safeDrawingPadding()) {
                    MainGraph(
                        navController = navHostController,
                        startDestination = if (selectedVoice.isEmpty()) Destination.Onboarding else Destination.Home
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