package pl.edu.agh.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.Greeting

enum class CourierNavigation(route: String) {
    OrdersList("orders_list");

    val route: String = AppNavigation.Courier.route + "/" + route
}

fun NavGraphBuilder.courierGraph(navController: NavHostController) {
    navigation(
        startDestination = CourierNavigation.OrdersList.route, route = AppNavigation.Courier.route
    ) {
        courierOrdersList()
    }
}

fun NavGraphBuilder.courierOrdersList() {
    composable(CourierNavigation.OrdersList.route) {
        // TODO: Implement Courier Home Screen
        Greeting(name = "COURIER")
    }
}
