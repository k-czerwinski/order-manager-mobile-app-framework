package pl.edu.agh.implementation.presentation.ui.courier

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
import pl.edu.agh.framework.presentation.ui.common.AppMenu
import pl.edu.agh.framework.presentation.ui.common.AppScreen
import pl.edu.agh.framework.presentation.ui.common.AppTopBar
import pl.edu.agh.implementation.presentation.viewmodel.CompanySuccessState
import pl.edu.agh.implementation.presentation.viewmodel.CompanyViewModel
import pl.edu.agh.implementation.presentation.navigation.CourierNavigation

@Composable
fun CourierTopBar(
    navController: NavController, companyViewModel: CompanyViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    val companyState by companyViewModel.companyState.collectAsState()
    val companyName = (companyState as? CompanySuccessState)?.data?.name ?: ""

    AppTopBar(
        navController = navController,
        companyName = "$companyName - ${stringResource(R.string.app_name)}",
        userMenu = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
            CourierMenu(
                navController = navController,
                showMenu = showMenu,
                onDismissRequest = { showMenu = false })
        }
    )
}

@Composable
fun CourierMenu(navController: NavController, showMenu: Boolean, onDismissRequest: () -> Unit) {
    AppMenu(showMenu = showMenu, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_orders)) },
            onClick = { navController.navigate(CourierNavigation.OrdersList.route) })
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_logout)) },
            onClick = { navController.navigate(CourierNavigation.Logout.route) })
    }
}

@Composable
fun LoggedInCourierLayout(
    navController: NavController, companyViewModel: CompanyViewModel,
    content: @Composable () -> Unit
) {
    AppScreen(topBar = {
        CourierTopBar(
            navController = navController,
            companyViewModel = companyViewModel
        )
    }, content = content)
}