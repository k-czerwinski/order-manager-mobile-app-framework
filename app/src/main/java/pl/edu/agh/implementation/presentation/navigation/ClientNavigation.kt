package pl.edu.agh.implementation.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.remote.ApiClient.logout
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.presentation.navigation.AuthNavigation
import pl.edu.agh.framework.viewmodel.sharedViewModel
import pl.edu.agh.framework.presentation.ui.common.UnexpectedErrorScreen
import pl.edu.agh.implementation.presentation.viewmodel.CompanyViewModel
import pl.edu.agh.implementation.presentation.viewmodel.CurrentUserViewModel
import pl.edu.agh.implementation.presentation.ui.client.ClientNewOrderScreen
import pl.edu.agh.implementation.presentation.ui.client.ClientOrderDetailsScreen
import pl.edu.agh.implementation.presentation.ui.client.ClientOrdersScreen
import pl.edu.agh.implementation.presentation.ui.client.LoggedInClientLayout
import pl.edu.agh.implementation.presentation.viewmodel.OrdersListViewModel
import pl.edu.agh.implementation.presentation.viewmodel.ProductListViewModel

enum class ClientNavigation(route: String) {
    OrdersList("orders_list"),
    OrderDetails("order_details/{orderId}"),
    CreateOrder("create_order"),
    Logout("logout"),
    UnexpectedError("unexpected_error");

    val route: String = CustomNavigation.Client.route + "/" + route

    companion object {
        fun createOrderDetailsRoute(orderId: Int) =
            "${CustomNavigation.Client.route}/order_details/$orderId"
    }
}

fun NavGraphBuilder.clientGraph(navController: NavHostController) {
    navigation(
        startDestination = ClientNavigation.OrdersList.route, route = CustomNavigation.Client.route
    ) {
        composable(ClientNavigation.OrdersList.route) {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val ordersListViewModel = it.sharedViewModel<OrdersListViewModel>(navController)
            val currentUserViewModel = it.sharedViewModel<CurrentUserViewModel>(navController)
            LoggedInClientLayout(navController, companyViewModel) {
                ClientOrdersScreen(navController, ordersListViewModel, currentUserViewModel)
            }
        }
        composable(ClientNavigation.OrderDetails.route) {
            val orderId = it.arguments?.getString("orderId")?.toInt()
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            LoggedInClientLayout(navController = navController, companyViewModel = companyViewModel) {
                ClientOrderDetailsScreen(navController, orderId!!)
            }
        }
        composable(ClientNavigation.CreateOrder.route) {
            val productListViewModel = it.sharedViewModel<ProductListViewModel>(navController)
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val ordersListViewModel = it.sharedViewModel<OrdersListViewModel>(navController)
            LoggedInClientLayout(navController, companyViewModel) {
                ClientNewOrderScreen(
                    navController,
                    productListViewModel,
                    ordersListViewModel)
            }
        }
        composable(ClientNavigation.Logout.route) {
            LaunchedEffect(Unit) {
                ApiClient.logout(EncryptedSharedPreferencesManager.getRefreshToken())
                EncryptedSharedPreferencesManager.eraseAllData()
                navController.navigate(AuthNavigation.BASE_ROUTE) {
                    popUpTo(CustomNavigation.Client.route) {
                        inclusive = true
                    }
                }
            }
        }
        composable(ClientNavigation.UnexpectedError.route) {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            LoggedInClientLayout(navController = navController, companyViewModel = companyViewModel) {
                UnexpectedErrorScreen()
            }
        }
    }
}
