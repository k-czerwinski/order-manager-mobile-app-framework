package pl.edu.agh.presentation.navigation

import OrderCreateViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.presentation.sharedViewModel
import pl.edu.agh.presentation.ui.client.ClientNewOrderScreen
import pl.edu.agh.presentation.ui.client.ClientOrderDetailsScreen
import pl.edu.agh.presentation.ui.client.LoggedInClientLayout
import pl.edu.agh.presentation.ui.client.ClientOrdersScreen
import pl.edu.agh.presentation.ui.common.UnexpectedErrorScreen
import pl.edu.agh.presentation.viewmodel.CompanyViewModel
import pl.edu.agh.presentation.viewmodel.OrderDetailsViewModel
import pl.edu.agh.presentation.viewmodel.OrdersListViewModel
import pl.edu.agh.presentation.viewmodel.ProductListViewModel
import pl.edu.agh.presentation.viewmodel.UserViewModel

enum class ClientNavigation(route: String) {
    OrdersList("orders_list"),
    OrderDetails("order_details/{orderId}"),
    CreateOrder("create_order"),
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
        startDestination = ClientNavigation.OrdersList.route, route = AppNavigation.Client.route
    ) {
        composable(ClientNavigation.OrdersList.route) {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val ordersListViewModel = it.sharedViewModel<OrdersListViewModel>(navController)
            val userViewModel = it.sharedViewModel<UserViewModel>(navController)
            LoggedInClientLayout(navController, companyViewModel) {
                ClientOrdersScreen(navController, ordersListViewModel, userViewModel)
            }
        }
        composable(ClientNavigation.OrderDetails.route) {
            val orderId = it.arguments?.getString("orderId")?.toInt()
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val orderDetailsViewModel = OrderDetailsViewModel(orderId!!)
            LoggedInClientLayout(navController = navController, companyViewModel = companyViewModel) {
                ClientOrderDetailsScreen(orderDetailsViewModel)
            }
        }
        composable(ClientNavigation.CreateOrder.route) {
            val productListViewModel = it.sharedViewModel<ProductListViewModel>(navController)
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val orderCreateViewModel = it.sharedViewModel<OrderCreateViewModel>(navController)
            val ordersListViewModel = it.sharedViewModel<OrdersListViewModel>(navController)

            orderCreateViewModel.resetOrderCreationState()
            LoggedInClientLayout(navController, companyViewModel) {
                ClientNewOrderScreen(
                    navController,
                    productListViewModel,
                    orderCreateViewModel,
                    ordersListViewModel
                )
            }
        }
        composable(ClientNavigation.Logout.route) {
            LaunchedEffect(Unit) {
                ApiClient.logout(EncryptedSharedPreferencesManager.getRefreshToken())
                EncryptedSharedPreferencesManager.clearUserData()
                navController.navigate(AppNavigation.Auth.route) {
                    popUpTo(AppNavigation.Client.route) {
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
