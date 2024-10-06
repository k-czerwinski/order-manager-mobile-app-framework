package pl.edu.agh.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.presentation.sharedViewModel
import pl.edu.agh.presentation.ui.common.UnexpectedErrorScreen
import pl.edu.agh.presentation.ui.courier.CourierOrderDetailsScreen
import pl.edu.agh.presentation.ui.courier.CourierOrderListScreen
import pl.edu.agh.presentation.ui.courier.CourierOrderSetExpectedDeliveryScreen
import pl.edu.agh.presentation.ui.courier.LoggedInCourierLayout
import pl.edu.agh.presentation.viewmodel.CompanyViewModel
import pl.edu.agh.presentation.viewmodel.OrdersListViewModel
import pl.edu.agh.presentation.viewmodel.UserViewModel

enum class CourierNavigation(route: String) {
    OrdersList("orders_list"),
    OrderDetails("order_details/{orderId}"),
    OrderExpectedDeliverySet("order_expected_delivery/{orderId}"),
    UnexpectedError("unexpected_error"),
    Logout("logout");

    val route: String = AppNavigation.Courier.route + "/" + route

    companion object {
        fun createOrderDetailsRoute(orderId: Int) =
            "${AppNavigation.Courier.route}/order_details/$orderId"

        fun navigateToOrderDetailsRoute(navController: NavHostController, orderId: Int) {
            navController.navigate(createOrderDetailsRoute(orderId)) {
                popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                    inclusive = true
                }
            }
        }

        fun navigateToOrderExpectedDeliveryRoute(navController: NavHostController, orderId: Int) {
            navController.navigate("${AppNavigation.Courier.route}/order_expected_delivery/$orderId") {
                popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                    inclusive = true
                }
            }
        }
    }
}

fun NavGraphBuilder.courierGraph(navController: NavHostController) {
    navigation(
        startDestination = CourierNavigation.OrdersList.route, route = AppNavigation.Courier.route
    ) {
        courierOrdersList(navController)
    }
}

fun NavGraphBuilder.courierOrdersList(navController: NavHostController) {
    composable(CourierNavigation.OrdersList.route) {
        val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
        val ordersListViewModel = it.sharedViewModel<OrdersListViewModel>(navController)
        val userViewModel = it.sharedViewModel<UserViewModel>(navController)
        LoggedInCourierLayout(navController, companyViewModel) {
            CourierOrderListScreen(navController, userViewModel, ordersListViewModel)
        }
    }

    composable(CourierNavigation.OrderDetails.route) {
        val orderId = it.arguments?.getString("orderId")?.toInt()
        val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
        val ordersListViewModel = it.sharedViewModel<OrdersListViewModel>(navController)
        LoggedInCourierLayout(navController = navController, companyViewModel = companyViewModel) {
            CourierOrderDetailsScreen(navController, orderId!!, ordersListViewModel)
        }
    }

    composable(CourierNavigation.OrderExpectedDeliverySet.route) {
        val orderId = it.arguments?.getString("orderId")?.toInt()
        val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
        LoggedInCourierLayout(navController = navController, companyViewModel = companyViewModel) {
            CourierOrderSetExpectedDeliveryScreen(navController, orderId!!)
        }
    }

    composable(CourierNavigation.UnexpectedError.route) {
        val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
        LoggedInCourierLayout(navController = navController, companyViewModel = companyViewModel) {
            UnexpectedErrorScreen()
        }
    }

    composable(CourierNavigation.Logout.route) {
        LaunchedEffect(Unit) {
            ApiClient.logout(EncryptedSharedPreferencesManager.getRefreshToken())
            EncryptedSharedPreferencesManager.clearUserData()
            navController.navigate(AppNavigation.Auth.route) {
                popUpTo(AppNavigation.Courier.route) {
                    inclusive = true
                }
            }
        }
    }
}
