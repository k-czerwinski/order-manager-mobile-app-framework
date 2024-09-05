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
import pl.edu.agh.model.OrderListViewItem
import pl.edu.agh.presentation.ui.common.AppMenu
import pl.edu.agh.presentation.ui.common.BackNavigationIcon
import pl.edu.agh.presentation.ui.common.AppScreen
import pl.edu.agh.presentation.ui.common.AppTopBar
import pl.edu.agh.presentation.ui.common.OrdersScreen
import pl.edu.agh.presentation.viewmodel.CompanyViewModel
import pl.edu.agh.presentation.viewmodel.OrdersViewModel
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
            onClick = { navController.navigate("client_orders") })
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_new_order)) },
            onClick = { navController.navigate("client_new_order") })
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_settings)) },
            onClick = { navController.navigate("client_settings") })
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_logout)) },
            onClick = { navController.navigate("client_logout") })
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
    ordersViewModel: OrdersViewModel,
    userViewModel: UserViewModel
) {
    val ordersState = ordersViewModel.ordersState.collectAsState()
    val orders = ordersState.value.let {
        if (it is OrdersViewModel.OrdersState.Success) it.orderDTOS.map(OrderListViewItem::fromOrder) else emptyList()
    }
    val userState = userViewModel.userState.collectAsState()
    val userName = userState.value.let {
        if (it is UserViewModel.UserState.Success) it.userDTO.firstName else "User"
    }

    OrdersScreen(userName, orders, onNewOrderClick = { navController.navigate("client_new_order") })
}