@file:OptIn(ExperimentalMaterial3Api::class)

package dev.mathroda.twelvereader.ui.navigation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.mathroda.twelvereader.ui.screens.apikey.SetApiKeyScreen
import dev.mathroda.twelvereader.ui.screens.home.HomeScreen
import dev.mathroda.twelvereader.ui.screens.mainplayer.MainPlayerScreen
import dev.mathroda.twelvereader.ui.screens.mainplayer.MainPlayerViewModel
import dev.mathroda.twelvereader.ui.screens.onboarding.OnboardingScreen
import dev.mathroda.twelvereader.ui.screens.selectvoice.SelectVoiceScreen
import dev.mathroda.twelvereader.ui.screens.writetext.WriteTextScreen
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel


@Serializable
sealed class Destination {
    @Serializable
    @SerialName("Onboarding")
    data object Onboarding : Destination()

    @Serializable
    @SerialName("Home")
    data object Home : Destination()

    @Serializable
    @SerialName("WriteText")
    data object WriteText : Destination()

    @Serializable
    @SerialName("MainPlayer")
    data object MainPlayer : Destination()

    @Serializable
    @SerialName("SelectVoice")
    data object SelectVoice: Destination()

    @Serializable
    @SerialName("SetApiKey")
    data object SetApiKey : Destination()
}

@ExperimentalLayoutApi
@Composable
fun MainGraph(
    navController: NavHostController,
    startDestination: Destination = Destination.Home
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Destination.Onboarding> {
            OnboardingScreen()
        }

        composable<Destination.Home> {
            HomeScreen(
                navigateToWriteText = { navController.navigate(Destination.WriteText) },
                navigateToSetApiKey = { navController.navigate(Destination.SetApiKey) }
            )
        }

        composable<Destination.WriteText> { backStackEntry ->
            val viewModel: MainPlayerViewModel = koinViewModel(viewModelStoreOwner = backStackEntry)

            WriteTextScreen(
                viewModel = viewModel,
                navigateBack = navController::navigateUp,
                navigateToMainPlayer = { navController.navigate(Destination.MainPlayer) }
            )
        }

        composable<Destination.MainPlayer> {
            val viewModel: MainPlayerViewModel = navController.previousBackStackEntry?.let {
                koinViewModel(viewModelStoreOwner = it)
            } ?: run { koinViewModel() }

            MainPlayerScreen(
                viewModel = viewModel,
                navigateBack = navController::navigateUp,
                didVoiceChange = navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>("didVoiceChange") ?: false,
                navigateToSelectVoice = { navController.navigate(Destination.SelectVoice) }
            )
        }

        composable<Destination.SelectVoice> {
            SelectVoiceScreen(
                navigateBack = { didVoiceChange ->
                    navController.previousBackStackEntry?.apply {
                        savedStateHandle["didVoiceChange"] = didVoiceChange
                    }
                    navController.navigateUp()
                }
            )
        }

        composable<Destination.SetApiKey> {
            SetApiKeyScreen(
                navigateBack = navController::navigateUp,
                isFirstInStack = navController.previousBackStackEntry == null
            )
        }
    }
}