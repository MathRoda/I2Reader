@file:OptIn(ExperimentalMaterial3Api::class)

package dev.mathroda.twelvereader.ui.navigation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.mathroda.twelvereader.ui.screens.home.HomeScreen
import dev.mathroda.twelvereader.ui.screens.onboarding.OnboardingScreen
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
                navigateBack = navController::navigateUp
            )
        }
    }
}