package pl.edu.agh.implementation.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import pl.edu.agh.framework.model.UserRole
import pl.edu.agh.framework.presentation.navigation.AuthNavigation
import pl.edu.agh.framework.presentation.navigation.authGraph

enum class CustomNavigation(val route: String, val navigationGraph: NavGraphBuilder.(NavHostController) -> Unit) {
    Client("client", NavGraphBuilder::clientGraph),
    Courier("courier", NavGraphBuilder::courierGraph),
    Admin("admin", NavGraphBuilder::adminGraph)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AuthNavigation.BASE_ROUTE) {
        authGraph(navController)
        UserRole.entries.map(UserRole::navigation).map(CustomNavigation::navigationGraph).forEach {
            it(navController)
        }
    }
}
