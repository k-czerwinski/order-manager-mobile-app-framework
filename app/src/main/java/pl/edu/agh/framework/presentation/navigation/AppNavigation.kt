package pl.edu.agh.framework.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import pl.edu.agh.framework.model.UserRoleDependencyInjector
import pl.edu.agh.framework.model.UserRoleInterface
import pl.edu.agh.framework.presentation.ui.theme.OrderManagerTheme

@Composable
fun AppNavigation() {
    OrderManagerTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = AuthNavigation.BASE_ROUTE) {
            authGraph(navController)
            UserRoleDependencyInjector.getUserRoleParser().values()
                .map(UserRoleInterface::navigation)
                .map(CustomNavigationInterface::navigationGraph).forEach {
                    it(navController)
                }
        }
    }
}