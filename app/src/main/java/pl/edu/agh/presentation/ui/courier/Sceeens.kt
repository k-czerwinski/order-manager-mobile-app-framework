package pl.edu.agh.presentation.ui.courier

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import pl.edu.agh.data.remote.dto.OrderListViewItemDTO
import pl.edu.agh.model.OrderListViewItem
import pl.edu.agh.presentation.navigation.CourierNavigation
import pl.edu.agh.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.presentation.ui.common.OrderDetailScreen
import pl.edu.agh.presentation.ui.common.OrderListScreen
import pl.edu.agh.presentation.viewmodel.CommonViewModel
import pl.edu.agh.presentation.viewmodel.OrderDetailsStateError
import pl.edu.agh.presentation.viewmodel.OrderDetailsStateSuccess
import pl.edu.agh.presentation.viewmodel.OrderDetailsViewModel
import pl.edu.agh.presentation.viewmodel.OrderSetExpectedDeliveryViewModel
import pl.edu.agh.presentation.viewmodel.OrdersListStateSuccess
import pl.edu.agh.presentation.viewmodel.OrdersListViewModel
import pl.edu.agh.presentation.viewmodel.UserStateSuccess
import pl.edu.agh.presentation.viewmodel.UserViewModel

@Composable
fun CourierOrderListScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    ordersListViewModel: OrdersListViewModel
) {
    val ordersState by ordersListViewModel.ordersListState.collectAsState()
    val orders: List<OrderListViewItemDTO> =
        (ordersState as? OrdersListStateSuccess)?.data ?: emptyList()
    val userState by userViewModel.userState.collectAsState()
    val userName = (userState as? UserStateSuccess)?.data?.firstName ?: "User"

    OrderListScreen(
        userName,
        orders.map(OrderListViewItem::fromDTO),
        navigateToOrderDetails = { navController.navigate(CourierNavigation.createOrderDetailsRoute(it)) },
        bottomButton = {}
    )
}

@Composable
fun CourierOrderDetailsScreen(
    navController: NavHostController,
    orderId: Int,
    ordersListViewModel: OrdersListViewModel
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
                CourierOrderDetailsActionButtons(navController, order, ordersListViewModel)
            }
        }

        is OrderDetailsStateError -> {
            navController.navigate(CourierNavigation.UnexpectedError.route)

        }

        else -> {
            CenteredCircularProgressIndicator()
        }
    }
}

@Composable
fun CourierOrderSetExpectedDeliveryScreen(
    navController: NavHostController,
    orderId: Int,
    orderSetExpectedDeliveryViewModel: OrderSetExpectedDeliveryViewModel = viewModel()
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
            OrderSetExpectedDelivery(navController, order!!, orderSetExpectedDeliveryViewModel)
        }

        is OrderDetailsStateError -> {
            navController.navigate(CourierNavigation.UnexpectedError.route)

        }

        else -> {
            CenteredCircularProgressIndicator()
        }
    }
}