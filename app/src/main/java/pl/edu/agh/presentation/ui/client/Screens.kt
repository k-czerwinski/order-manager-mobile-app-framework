package pl.edu.agh.presentation.ui.client

import OrderCreateViewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import pl.edu.agh.data.remote.dto.OrderListViewItemDTO
import pl.edu.agh.model.OrderListViewItem
import pl.edu.agh.presentation.navigation.ClientNavigation
import pl.edu.agh.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.presentation.ui.common.OrderDetailScreen
import pl.edu.agh.presentation.ui.common.OrdersScreen
import pl.edu.agh.presentation.ui.common.UnexpectedErrorScreen
import pl.edu.agh.presentation.viewmodel.OrderDetailsViewModel
import pl.edu.agh.presentation.viewmodel.OrdersListViewModel
import pl.edu.agh.presentation.viewmodel.ProductListViewModel
import pl.edu.agh.presentation.viewmodel.UserViewModel

@Composable
fun ClientOrdersScreen(
    navController: NavController,
    ordersListViewModel: OrdersListViewModel,
    userViewModel: UserViewModel
) {
    val ordersState by ordersListViewModel.ordersListState.collectAsState()
    val orders: List<OrderListViewItemDTO> =
        (ordersState as? OrdersListViewModel.OrdersListState.Success)?.orderDTOS ?: emptyList()
    val userState by userViewModel.userState.collectAsState()
    val userName = (userState as? UserViewModel.UserState.Success)?.userDTO?.firstName ?: "User"

    OrdersScreen(
        userName,
        orders.map(OrderListViewItem::fromDTO),
        onNewOrderClick = { navController.navigate(ClientNavigation.CreateOrder.route) },
        navController
    )
}

@Composable
fun ClientOrderDetailsScreen(
    navController: NavController,
    orderDetailsViewModel: OrderDetailsViewModel
) {
    val orderDetailsState by orderDetailsViewModel.orderDetailsState.collectAsState()
    when (orderDetailsState) {
        is OrderDetailsViewModel.OrderDetailsState.Success -> {
            val order =
                (orderDetailsState as? OrderDetailsViewModel.OrderDetailsState.Success)?.order
            OrderDetailScreen(order!!)
        }

        is OrderDetailsViewModel.OrderDetailsState.Error -> {
            UnexpectedErrorScreen()
        }

        is OrderDetailsViewModel.OrderDetailsState.Empty -> {
            CenteredCircularProgressIndicator()
        }
    }
}

@Composable
fun ClientNewOrderScreen(
    navController: NavController, productListViewModel: ProductListViewModel,
    orderCreateViewModel: OrderCreateViewModel,
    ordersListViewModel: OrdersListViewModel
) {
    val orderCreateState by orderCreateViewModel.orderCreationState.collectAsState()
    val productListState by productListViewModel.productsListState.collectAsState()
    when (productListState) {
        is ProductListViewModel.ProductListState.Success -> {
            val products =
                (productListState as? ProductListViewModel.ProductListState.Success)?.products
            CreateNewOrderScreen(products!!, orderCreateViewModel)
            when (orderCreateState) {
                is OrderCreateViewModel.OrderCreationState.Initial -> {}
                is OrderCreateViewModel.OrderCreationState.NoProductSelectedError -> {
                    NoProductSelectedDialog(backToNewOrderScreen = { orderCreateViewModel.resetOrderCreationState() })
                }

                is OrderCreateViewModel.OrderCreationState.Loading -> {
                    CenteredCircularProgressIndicator()
                }

                is OrderCreateViewModel.OrderCreationState.Success -> {
                    OrderConfirmedDialog(backToOrderListView = {
                        ordersListViewModel.refreshOrders()
                        navController.navigate(
                            ClientNavigation.OrdersList.route
                        ) {
                            popUpTo(ClientNavigation.OrdersList.route) {
                                inclusive = true
                            }
                        }
                    })
                }

                is OrderCreateViewModel.OrderCreationState.Error -> {
                    Dialog(onDismissRequest = {}, content = { Text("Error") })
                }
            }
        }

        is ProductListViewModel.ProductListState.Error -> {
            UnexpectedErrorScreen()
        }

        is ProductListViewModel.ProductListState.Empty -> {
            CenteredCircularProgressIndicator()
        }
    }

}