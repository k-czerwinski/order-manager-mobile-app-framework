package pl.edu.agh.implementation.presentation.ui.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import pl.edu.agh.framework.model.OrderListViewItem
import pl.edu.agh.framework.model.Product
import pl.edu.agh.framework.model.UserListViewItem
import pl.edu.agh.framework.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.framework.presentation.ui.common.OrderDetailScreen
import pl.edu.agh.framework.presentation.ui.common.OrderListScreen
import pl.edu.agh.framework.presentation.ui.common.ProductList
import pl.edu.agh.framework.presentation.ui.common.UserListScreen
import pl.edu.agh.framework.presentation.ui.common.UserDetails
import pl.edu.agh.framework.viewmodel.CommonViewModel
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
import pl.edu.agh.implementation.presentation.viewmodel.CurrentUserViewModel
import pl.edu.agh.implementation.presentation.viewmodel.ProductListStateSuccess
import pl.edu.agh.implementation.presentation.viewmodel.ProductListViewModel
import pl.edu.agh.implementation.presentation.viewmodel.UserDetailsSuccessState
import pl.edu.agh.implementation.presentation.viewmodel.UserDetailsViewModel

@Composable
fun AdminOrderListScreen(
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

    UserListScreen(users, onUserClick = {
        navController.navigate(AdminNavigation.createUserDetailsRoute(it.id))
    }) {
        UserListBottomButton(onClick = {
            navController.navigate(AdminNavigation.CreateUser.route)
        })
    }
}

@Composable
fun AdminUserDetailsScreen(
    navController: NavHostController,
    userId: Int
) {
    val currentUserViewModel: UserDetailsViewModel = viewModel(
        factory = CommonViewModel.provideFactory {
            UserDetailsViewModel(userId)
        }
    )
    val userState by currentUserViewModel.userDetailsState.collectAsState()
    when (userState) {
        is UserDetailsSuccessState -> {
            UserDetails((userState as UserDetailsSuccessState).data)
        }

        is CommonViewModel.State.Error -> {
            navController.navigate(AdminNavigation.UnexpectedError.route)
        }

        is CommonViewModel.State.Loading -> {
            CenteredCircularProgressIndicator()
        }

        is CommonViewModel.State.Empty -> {}
    }
}

@Composable
fun ProductListScreen(
    navController: NavHostController,
    productListViewModel: ProductListViewModel
) {
    val productListState by productListViewModel.productsListState.collectAsState()
    val products: List<Product> =
        (productListState as? ProductListStateSuccess)?.data ?: emptyList()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ProductList(products)
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            CreateProductButton(onClick = {
                navController.navigate(AdminNavigation.CreateProduct.route)
            })
        }
    }
}

@Composable
fun AddProductScreen(navController: NavHostController, productListViewModel: ProductListViewModel) {
    AddProductForm(navController, productListViewModel)
}