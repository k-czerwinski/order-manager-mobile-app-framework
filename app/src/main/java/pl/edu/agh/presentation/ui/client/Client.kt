package pl.edu.agh.presentation.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import pl.edu.agh.R
import pl.edu.agh.presentation.ui.common.AppMenu
import pl.edu.agh.presentation.ui.common.BackNavigationIcon
import pl.edu.agh.presentation.ui.common.AppScreen
import pl.edu.agh.presentation.ui.common.AppTopBar
import pl.edu.agh.presentation.viewmodel.CompanyViewModel

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
@Preview
fun MainScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Red)
        ) {
            Text("1", modifier = Modifier.align(Alignment.Center))
        }
        Box(
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth()
                .background(Color.Green)
        ) {
            Text("3", modifier = Modifier.align(Alignment.Center))
        }
        Box(
            modifier = Modifier
                .weight(5f)
                .fillMaxWidth()
                .background(Color.Blue)
        ) {
            Text("5", modifier = Modifier.align(Alignment.Center))
        }
    }
}