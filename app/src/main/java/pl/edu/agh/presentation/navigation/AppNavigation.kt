package pl.edu.agh.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

enum class AppNavigation(val route: String) {
    Auth("auth"),
    Client("client"),
    Courier("courier")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppNavigation.Auth.route) {
        authGraph(navController)
        clientGraph(navController)
        courierGraph(navController)
    }
}