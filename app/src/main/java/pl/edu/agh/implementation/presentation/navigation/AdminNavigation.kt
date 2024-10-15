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
import pl.edu.agh.framework.presentation.sharedViewModel
import pl.edu.agh.framework.presentation.ui.common.UnexpectedErrorScreen
import pl.edu.agh.implementation.presentation.ui.admin.AddProductScreen
import pl.edu.agh.implementation.presentation.ui.admin.AddUserScreen
import pl.edu.agh.implementation.presentation.ui.admin.AdminOrderDetailsScreen
import pl.edu.agh.implementation.presentation.ui.admin.AdminOrderListScreen
import pl.edu.agh.implementation.presentation.ui.admin.AdminSendOrderScreen
import pl.edu.agh.implementation.presentation.ui.admin.AdminUserDetailsScreen
import pl.edu.agh.implementation.presentation.ui.admin.AdminUserListScreen
import pl.edu.agh.implementation.presentation.ui.admin.LoggedInAdminLayout
import pl.edu.agh.implementation.presentation.ui.admin.ProductListScreen
import pl.edu.agh.implementation.presentation.ui.courier.LoggedInCourierLayout
import pl.edu.agh.implementation.presentation.viewmodel.CompanyViewModel
import pl.edu.agh.implementation.presentation.viewmodel.CourierListViewModel
import pl.edu.agh.implementation.presentation.viewmodel.OrdersListViewModel
import pl.edu.agh.implementation.presentation.viewmodel.UserListViewModel
import pl.edu.agh.implementation.presentation.viewmodel.CurrentUserViewModel
import pl.edu.agh.implementation.presentation.viewmodel.ProductListViewModel

enum class AdminNavigation(route: String) {
    OrderList("order_list"),
    OrderDetails("order_details/{orderId}"),
    SendOrder("send_order/{orderId}"),
    ProductList("product_list"),
    CreateProduct("create_product"),
    UserList("user_list"),
    UserDetails("user_details/{userId}"),
    CreateUser("create_user"),
    Logout("logout"),
    UnexpectedError("unexpected_error");

    val route: String = CustomNavigation.Admin.route + "/" + route

    companion object {
        fun createOrderDetailsRoute(orderId: Int) =
            "${CustomNavigation.Admin.route}/order_details/$orderId"

        fun createSendOrderRoute(orderId: Int) =
            "${CustomNavigation.Admin.route}/send_order/$orderId"

        fun createUserDetailsRoute(userId: Int) =
            "${CustomNavigation.Admin.route}/user_details/$userId"
    }
}
fun NavGraphBuilder.adminGraph(navController: NavHostController) {
    navigation(
        startDestination = AdminNavigation.OrderList.route, route = CustomNavigation.Admin.route
    ) {
        composable(AdminNavigation.OrderList.route) {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val ordersListViewModel = it.sharedViewModel<OrdersListViewModel>(navController)
            val currentUserViewModel = it.sharedViewModel<CurrentUserViewModel>(navController)
            LoggedInAdminLayout(navController, companyViewModel) {
                AdminOrderListScreen(navController, ordersListViewModel, currentUserViewModel)
            }
        }
        composable(AdminNavigation.OrderDetails.route) {
            val orderId = it.arguments?.getString("orderId")?.toInt()
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            LoggedInAdminLayout(
                navController = navController,
                companyViewModel = companyViewModel
            ) {
                AdminOrderDetailsScreen(navController, orderId!!)
            }
        }

        composable(AdminNavigation.SendOrder.route) {
            val orderId = it.arguments?.getString("orderId")?.toInt()
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val courierListViewModel = it.sharedViewModel<CourierListViewModel>(navController)
            val ordersListViewModel = it.sharedViewModel<OrdersListViewModel>(navController)
            LoggedInAdminLayout(
                navController = navController,
                companyViewModel = companyViewModel
            ) {
                AdminSendOrderScreen(navController, orderId!!, courierListViewModel, ordersListViewModel)
            }
        }

        composable(AdminNavigation.UserList.route) {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val userListViewModel = it.sharedViewModel<UserListViewModel>(navController)
            LoggedInAdminLayout(
                navController = navController,
                companyViewModel = companyViewModel
            ) {
                AdminUserListScreen(navController, userListViewModel)
            }
        }

        composable(AdminNavigation.UserDetails.route) {
            val userId = it.arguments?.getString("userId")?.toInt()
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            LoggedInAdminLayout(
                navController = navController,
                companyViewModel = companyViewModel
            ) {
                AdminUserDetailsScreen(navController, userId!!)
            }
        }

        composable(AdminNavigation.CreateUser.route) {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            LoggedInAdminLayout(
                navController = navController,
                companyViewModel = companyViewModel
            ) {
                AddUserScreen(navController)
            }
        }

        composable(AdminNavigation.ProductList.route) {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            val productListViewModel = it.sharedViewModel<ProductListViewModel>(navController)
            LoggedInAdminLayout(
                navController = navController,
                companyViewModel = companyViewModel
            ) {
                ProductListScreen(navController, productListViewModel)
            }
        }

        composable(AdminNavigation.CreateProduct.route) {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            LoggedInAdminLayout(
                navController = navController,
                companyViewModel = companyViewModel
            ) {
                AddProductScreen(navController)
            }
        }

        composable(AdminNavigation.UnexpectedError.route) {
            val companyViewModel = it.sharedViewModel<CompanyViewModel>(navController)
            LoggedInCourierLayout(
                navController = navController,
                companyViewModel = companyViewModel
            ) {
                UnexpectedErrorScreen()
            }
        }

        composable(AdminNavigation.Logout.route) {
            LaunchedEffect(Unit) {
                ApiClient.logout(EncryptedSharedPreferencesManager.getRefreshToken())
                EncryptedSharedPreferencesManager.eraseAllData()
                navController.navigate(AuthNavigation.BASE_ROUTE) {
                    popUpTo(CustomNavigation.Admin.route) {
                        inclusive = true
                    }
                }
            }
        }
    }
}