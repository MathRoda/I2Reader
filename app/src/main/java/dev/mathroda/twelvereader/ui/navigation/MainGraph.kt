@file:OptIn(ExperimentalMaterial3Api::class)

package dev.mathroda.twelvereader.ui.navigation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.mathroda.twelvereader.ui.screens.home.HomeScreen
import dev.mathroda.twelvereader.ui.screens.mainplayer.MainPlayerScreen
import dev.mathroda.twelvereader.ui.screens.onboarding.OnboardingScreen
import dev.mathroda.twelvereader.ui.screens.selectvoice.SelectVoiceScreen
import dev.mathroda.twelvereader.ui.screens.writetext.WriteTextScreen
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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
    data class MainPlayer(val uri: String) : Destination()

    @Serializable
    @SerialName("SelectVoice")
    data object SelectVoice: Destination()
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
                navigateToWriteText = { navController.navigate(Destination.WriteText) }
            )
        }

        composable<Destination.WriteText> {
            WriteTextScreen(
                navigateBack = navController::navigateUp,
                navigateToMainPlayer = { uri, text ->
                    navController.currentBackStackEntry?.apply {
                        savedStateHandle["text"] = text
                    }

                    navController.navigate(Destination.MainPlayer(uri))
                }
            )
        }

        composable<Destination.MainPlayer> { backStackEntry ->
            val route: Destination.MainPlayer = backStackEntry.toRoute()
            MainPlayerScreen(
                uri = route.uri,
                text = navController.previousBackStackEntry?.savedStateHandle?.get<String>("text") ?: "",
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
    }
}