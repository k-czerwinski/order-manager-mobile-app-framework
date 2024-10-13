package pl.edu.agh.implementation.presentation.ui.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import pl.edu.agh.framework.model.OrderListViewItem
import pl.edu.agh.framework.model.UserListViewItem
import pl.edu.agh.framework.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.framework.presentation.ui.common.OrderDetailScreen
import pl.edu.agh.framework.presentation.ui.common.OrderListScreen
import pl.edu.agh.framework.presentation.ui.common.UserListScreen
import pl.edu.agh.framework.presentation.viewmodel.CommonViewModel
import pl.edu.agh.implementation.presentation.navigation.AdminNavigation
import pl.edu.agh.implementation.presentation.viewmodel.CourierListViewModel
import pl.edu.agh.implementation.presentation.viewmodel.OrderDetailsStateError
import pl.edu.agh.implementation.presentation.viewmodel.OrderDetailsStateSuccess
import pl.edu.agh.implementation.presentation.viewmodel.OrderDetailsViewModel
import pl.edu.agh.implementation.presentation.viewmodel.OrderSendViewModel
import pl.edu.agh.implementation.presentation.viewmodel.OrdersListStateSuccess
import pl.edu.agh.implementation.presentation.viewmodel.OrdersListViewModel
import pl.edu.agh.implementation.presentation.viewmodel.UserListStateSuccess
import pl.edu.agh.implementation.presentation.viewmodel.UserListViewModel
import pl.edu.agh.implementation.presentation.viewmodel.UserStateSuccess
import pl.edu.agh.implementation.presentation.viewmodel.UserViewModel

@Composable
fun AdminOrderListScreen(
    navController: NavController,
    ordersListViewModel: OrdersListViewModel,
    userViewModel: UserViewModel
) {
    val ordersState by ordersListViewModel.ordersListState.collectAsState()
    val orders: List<OrderListViewItem> =
        (ordersState as? OrdersListStateSuccess)?.data ?: emptyList()
    val userState by userViewModel.userState.collectAsState()
    val userName = (userState as? UserStateSuccess)?.data?.firstName ?: "User"

    OrderListScreen(
        userName,
        orders,
        navigateToOrderDetails = {
            navController.navigate(AdminNavigation.createOrderDetailsRoute(it))
        },
        bottomButton = {}
    )
}

@Composable
fun AdminOrderDetailsScreen(
    navController: NavHostController,
    orderId: Int
) {
    val orderDetailsViewModel: OrderDetailsViewModel = viewModel(
        factory = CommonViewModel.provideFactory {
            OrderDetailsViewModel(orderId)
        }
    )
    val orderDetailsState by orderDetailsViewModel.orderDetailsState.collectAsState()
    when (orderDetailsState) {
        is OrderDetailsStateSuccess -> {
            val order = (orderDetailsState as? OrderDetailsStateSuccess)?.data
            OrderDetailScreen(order!!) {
                AdminOrderDetailsActionButton(navController, order)
            }
        }

        is OrderDetailsStateError -> {
            navController.navigate(AdminNavigation.UnexpectedError.route)
        }

        else -> {
            CenteredCircularProgressIndicator()
        }
    }
}

@Composable
fun AdminSendOrderScreen(
    navController: NavHostController,
    orderId: Int,
    courierListViewModel: CourierListViewModel,
    orderListViewModel: OrdersListViewModel,
    orderSendViewModel: OrderSendViewModel = viewModel()
) {
    val userListState by courierListViewModel.userListState.collectAsState()
    val orderDetailsViewModel: OrderDetailsViewModel = viewModel(
        factory = CommonViewModel.provideFactory {
            OrderDetailsViewModel(orderId)
        }
    )
    val orderDetailsState by orderDetailsViewModel.orderDetailsState.collectAsState()

    if (userListState is UserListStateSuccess && orderDetailsState is OrderDetailsStateSuccess) {
        val order = (orderDetailsState as? OrderDetailsStateSuccess)?.data
        val courierList = (userListState as? UserListStateSuccess)?.data
        SendOrder(navController, order!!, courierList!!, orderSendViewModel, orderListViewModel)
    } else if (userListState is CommonViewModel.State.Loading || orderDetailsState is CommonViewModel.State.Loading) {
        CenteredCircularProgressIndicator()

    } else {
        navController.navigate(AdminNavigation.UnexpectedError.route)
    }
}

@Composable
fun AdminUserListScreen(
    navController: NavHostController,
    userListViewModel: UserListViewModel
) {
    val userListState by userListViewModel.userListState.collectAsState()
    val users: List<UserListViewItem> =
        (userListState as? UserListStateSuccess)?.data ?: emptyList()

    UserListScreen(users, onUserClick = {})
}