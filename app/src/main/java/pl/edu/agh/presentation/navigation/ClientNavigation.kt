package pl.edu.agh.presentation.navigation

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.presentation.sharedViewModel
import pl.edu.agh.presentation.ui.client.ClientOrderDetailsScreen
import pl.edu.agh.presentation.ui.client.LoggedInUserLayout
import pl.edu.agh.presentation.ui.client.ClientOrdersScreen
import pl.edu.agh.presentation.ui.common.UnexpectedErrorScreen
import pl.edu.agh.presentation.viewmodel.CompanyViewModel
import pl.edu.agh.presentation.viewmodel.OrderDetailsViewModel
import pl.edu.agh.presentation.viewmodel.OrdersListViewModel
import pl.edu.agh.presentation.viewmodel.UserViewModel

enum class ClientNavigation(route: String) {
    OrderList("order_list"),
    OrderDetails("order_details/{orderId}"),
    CreateOrder("create_order"),
    Settings("settings"),
    Logout("logout"),
    UnexpectedError("unexpected_error");

    val route: String = AppNavigation.Client.route + "/" + route

    companion object {
        fun createOrderDetailsRoute(orderId: Int) =
            "${AppNavigation.Client.route}/order_details/$orderId"
    }
}

fun NavGraphBuilder.clientGraph(navController: NavHostController) {
    navigation(
        startDestination = ClientNavigation.OrderList.route, route = AppNavigation.Client.route
    ) {
        composable(ClientNavigation.OrderList.route) {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val ordersListViewModel = it.sharedViewModel<OrdersListViewModel>(navController)
            val userViewModel = it.sharedViewModel<UserViewModel>(navController)
            LoggedInUserLayout(navController, companyViewModel) {
                ClientOrdersScreen(navController, ordersListViewModel, userViewModel)
            }
        }
        composable(ClientNavigation.OrderDetails.route) {
            val orderId = it.arguments?.getString("orderId")?.toInt()
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val orderDetailsViewModel = OrderDetailsViewModel(orderId!!)
            LoggedInUserLayout(navController = navController, companyViewModel = companyViewModel) {
                ClientOrderDetailsScreen(navController, orderDetailsViewModel)
            }
        }
        composable(ClientNavigation.CreateOrder.route) {
            Text(text = "Client new order")
        }
        composable(ClientNavigation.Settings.route) {

        }
        composable(ClientNavigation.Logout.route) {

        }
        composable(ClientNavigation.UnexpectedError.route) {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            LoggedInUserLayout(navController = navController, companyViewModel = companyViewModel) {
                UnexpectedErrorScreen()
            }
        }
    }
}
