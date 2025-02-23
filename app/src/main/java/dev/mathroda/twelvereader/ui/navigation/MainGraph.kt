package dev.mathroda.twelvereader.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.mathroda.twelvereader.ui.screens.onboarding.OnboardingScreen
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
}

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
            Text("Home")
        }
    }
}