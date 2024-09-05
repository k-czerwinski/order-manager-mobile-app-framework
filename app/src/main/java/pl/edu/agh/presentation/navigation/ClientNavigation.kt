package pl.edu.agh.presentation.navigation

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.presentation.sharedViewModel
import pl.edu.agh.presentation.ui.client.LoggedInUserLayout
import pl.edu.agh.presentation.ui.client.ClientOrdersScreen
import pl.edu.agh.presentation.viewmodel.CompanyViewModel
import pl.edu.agh.presentation.viewmodel.OrdersViewModel
import pl.edu.agh.presentation.viewmodel.UserViewModel

fun NavGraphBuilder.clientGraph(navController: NavHostController) {
    navigation(
        startDestination = "client_orders", route = "client_route"
    ) {
        composable("client_orders") {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val ordersViewModel = it.sharedViewModel<OrdersViewModel>(navController)
            val userViewModel = it.sharedViewModel<UserViewModel>(navController)
            LoggedInUserLayout(navController, companyViewModel) {
                ClientOrdersScreen(navController, ordersViewModel, userViewModel)
            }
        }
        composable("client_order_view") {
            Text(text = "Client order view")
        }
        composable("client_new_order") {
            Text(text = "Client new order")
        }
        composable("client_settings") {

        }
        composable("client_logout") {

        }
    }
}
