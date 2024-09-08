@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package pl.edu.agh.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AppScreen(topBar: @Composable () -> Unit, content: @Composable () -> Unit) {
    Scaffold(
        topBar = topBar,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                content()
            }
        }
    )
}


@Composable
fun AppTopBar(
    companyName: String,
    userMenu: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                companyName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = navigationIcon,
        actions = {
            userMenu()
        },
    )
}

@Composable
fun AppMenu(showMenu: Boolean, onDismissRequest: () -> Unit, menuItems: @Composable () -> Unit) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(Color.White)
            .width(200.dp)
    ) {
        menuItems()
    }
}

@Composable
fun BackNavigationIcon(navController: NavController) {
    if (navController.previousBackStackEntry != null) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous screen"
            )
        }
    }
}

@Composable
fun CenteredCircularProgressIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun UnexpectedErrorScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Oops! Something went wrong.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "We couldn't process your request. Please try again later.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}