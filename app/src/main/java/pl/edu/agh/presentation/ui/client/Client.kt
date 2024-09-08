package pl.edu.agh.presentation.ui.client

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import pl.edu.agh.R
import pl.edu.agh.data.remote.dto.OrderListViewItemDTO
import pl.edu.agh.model.OrderListViewItem
import pl.edu.agh.presentation.navigation.ClientNavigation
import pl.edu.agh.presentation.ui.common.AppMenu
import pl.edu.agh.presentation.ui.common.BackNavigationIcon
import pl.edu.agh.presentation.ui.common.AppScreen
import pl.edu.agh.presentation.ui.common.AppTopBar
import pl.edu.agh.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.presentation.ui.common.OrderDetailScreen
import pl.edu.agh.presentation.ui.common.OrdersScreen
import pl.edu.agh.presentation.ui.common.UnexpectedErrorScreen
import pl.edu.agh.presentation.viewmodel.CompanyViewModel
import pl.edu.agh.presentation.viewmodel.OrderDetailsViewModel
import pl.edu.agh.presentation.viewmodel.OrdersListViewModel
import pl.edu.agh.presentation.viewmodel.UserViewModel

@Composable
fun ClientTopBar(
    navController: NavController, companyViewModel: CompanyViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    val companyState = companyViewModel.companyState.collectAsState()
    val companyName = companyState.value.let {
        if (it is CompanyViewModel.CompanyState.Success) it.company.name else ""
    }
    AppTopBar(
        companyName = "$companyName - ${stringResource(R.string.app_name)}",
        userMenu = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
            ClientMenu(
                navController = navController,
                showMenu = showMenu,
                onDismissRequest = { showMenu = false })
        },
        navigationIcon = {
            BackNavigationIcon(navController)
        }
    )
}

@Composable
fun ClientMenu(navController: NavController, showMenu: Boolean, onDismissRequest: () -> Unit) {
    AppMenu(showMenu = showMenu, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_orders)) },
            onClick = { navController.navigate(ClientNavigation.OrderList.route) })
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_new_order)) },
            onClick = { navController.navigate(ClientNavigation.CreateOrder.route) })
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_settings)) },
            onClick = { navController.navigate(ClientNavigation.Settings.route) })
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_logout)) },
            onClick = { navController.navigate(ClientNavigation.Logout.route) })
    }
}

@Composable
fun LoggedInUserLayout(
    navController: NavController, companyViewModel: CompanyViewModel,
    content: @Composable () -> Unit
) {
    AppScreen(topBar = {
        ClientTopBar(
            navController = navController,
            companyViewModel = companyViewModel
        )
    }, content = content)
}

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
        orders.map(OrderListViewItem::fromOrderListViewItemDTO),
        onNewOrderClick = { navController.navigate(ClientNavigation.CreateOrder.route) },
        navController)
}

@Composable
fun ClientOrderDetailsScreen(
    navController: NavController,
    orderDetailsViewModel: OrderDetailsViewModel
) {
    val orderDetailsState by orderDetailsViewModel.orderDetailsState.collectAsState()
    when (orderDetailsState) {
        is OrderDetailsViewModel.OrderDetailsState.Success -> {
            val order = (orderDetailsState as? OrderDetailsViewModel.OrderDetailsState.Success)?.order
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