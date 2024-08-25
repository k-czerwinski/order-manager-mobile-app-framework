package pl.edu.agh.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "auth_route") {
        authGraph(navController)
        clientGraph(navController)
        courierGraph(navController)
    }
}