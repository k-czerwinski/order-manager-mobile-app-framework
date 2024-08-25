package pl.edu.agh.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.Greeting

fun NavGraphBuilder.clientGraph(navController: NavHostController) {
    navigation(
        startDestination = "client_home", route = "client_route"
    ) {
        clientHomeScreen()
    }
}

fun NavGraphBuilder.clientHomeScreen() {
    composable("client_home") {
        // TODO: Implement Client Home Screen
        Greeting(name = "client")

    }
}
