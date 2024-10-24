package pl.edu.agh.implementation.presentation.ui.client

import pl.edu.agh.implementation.presentation.viewmodel.OrderCreateViewModel
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pl.edu.agh.R
import pl.edu.agh.framework.model.OrderListViewItem
import pl.edu.agh.framework.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.framework.presentation.ui.common.OrderDetailScreen
import pl.edu.agh.framework.presentation.ui.common.OrderListScreen
import pl.edu.agh.framework.viewmodel.CommonViewModel
import pl.edu.agh.implementation.presentation.viewmodel.UserStateSuccess
import pl.edu.agh.implementation.presentation.viewmodel.CurrentUserViewModel
import pl.edu.agh.implementation.presentation.navigation.ClientNavigation
import pl.edu.agh.implementation.presentation.viewmodel.OrderDetailsStateError
import pl.edu.agh.implementation.presentation.viewmodel.OrderDetailsStateSuccess
import pl.edu.agh.implementation.presentation.viewmodel.OrderDetailsViewModel
import pl.edu.agh.implementation.presentation.viewmodel.OrdersListStateSuccess
import pl.edu.agh.implementation.presentation.viewmodel.OrdersListViewModel
import pl.edu.agh.implementation.presentation.viewmodel.ProductListStateError
import pl.edu.agh.implementation.presentation.viewmodel.ProductListStateSuccess
import pl.edu.agh.implementation.presentation.viewmodel.ProductListViewModel

@Composable
fun ClientOrdersScreen(
    navController: NavController,
    ordersListViewModel: OrdersListViewModel,
    currentUserViewModel: CurrentUserViewModel
) {
    val ordersState by ordersListViewModel.ordersListState.collectAsState()
    val orders: List<OrderListViewItem> =
        (ordersState as? OrdersListStateSuccess)?.data ?: emptyList()
    val userState by currentUserViewModel.userState.collectAsState()
    val userName = (userState as? UserStateSuccess)?.data?.firstName ?: "User"

    OrderListScreen(
        userName,
        orders,
        navigateToOrderDetails = { navController.navigate(ClientNavigation.createOrderDetailsRoute(it)) }
    ) {
        Button(
            onClick = { navController.navigate(ClientNavigation.CreateOrder.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.button_new_order))
        }
    }
}

@Composable
fun ClientOrderDetailsScreen(navController: NavController, orderId: Int) {
    val orderDetailsViewModel: OrderDetailsViewModel = viewModel(
        factory = CommonViewModel.provideFactory {
            OrderDetailsViewModel(orderId)
        }
    )
    val orderDetailsState by orderDetailsViewModel.orderDetailsState.collectAsState()
    when (orderDetailsState) {
        is OrderDetailsStateSuccess -> {
            val order = (orderDetailsState as? OrderDetailsStateSuccess)?.data
            OrderDetailScreen(order!!, actionButtons = {})
        }

        is OrderDetailsStateError -> {
            navController.navigate(ClientNavigation.UnexpectedError.route)
        }
        else -> {
            CenteredCircularProgressIndicator()
        }
    }
}

@Composable
fun ClientNewOrderScreen(
    navController: NavController, productListViewModel: ProductListViewModel,
    ordersListViewModel: OrdersListViewModel,
    orderCreateViewModel: OrderCreateViewModel = viewModel()
) {
    val orderCreateState by orderCreateViewModel.orderCreationState.collectAsState()
    val productListState by productListViewModel.productsListState.collectAsState()
    when (productListState) {
        is ProductListStateSuccess -> {
            val products =
                (productListState as? ProductListStateSuccess)?.data
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
                        ordersListViewModel.loadOrders()
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
                    navController.navigate(ClientNavigation.UnexpectedError.route)
                }
            }
        }

        is ProductListStateError -> {
            navController.navigate(ClientNavigation.UnexpectedError.route)
        }

        else -> {
            CenteredCircularProgressIndicator()
        }
    }

}