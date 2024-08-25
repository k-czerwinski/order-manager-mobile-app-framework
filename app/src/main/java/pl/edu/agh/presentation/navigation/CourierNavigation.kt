package pl.edu.agh.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.Greeting

fun NavGraphBuilder.courierGraph(navController: NavHostController) {
    navigation(
        startDestination = "courier_home", route = "courier_route"
    ) {
        courierHomeScreen()
    }
}

fun NavGraphBuilder.courierHomeScreen() {
    composable("courier_home") {
        // TODO: Implement Courier Home Screen
        Greeting(name = "COURIER")
    }
}
