package pl.edu.agh.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.presentation.sharedViewModel
import pl.edu.agh.presentation.ui.client.LoggedInUserLayout
import pl.edu.agh.presentation.ui.client.MainScreen
import pl.edu.agh.presentation.viewmodel.CompanyViewModel

fun NavGraphBuilder.clientGraph(navController: NavHostController) {
    navigation(
        startDestination = "client_home", route = "client_route"
    ) {
        composable("client_home") {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            LoggedInUserLayout(navController = navController, companyViewModel) {
                MainScreen()
            }
        }
        composable("client_orders") {

        }
        composable("client_new_order") {

        }
        composable("client_settings") {

        }
        composable("client_logout") {

        }
    }
}
