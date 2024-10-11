package pl.edu.agh.implementation.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import pl.edu.agh.framework.presentation.navigation.CustomNavigationInterface

enum class CustomNavigation(
    override val route: String,
    override val navigationGraph: NavGraphBuilder.(NavHostController) -> Unit
) :
    CustomNavigationInterface {
    Client("client", NavGraphBuilder::clientGraph),
    Courier("courier", NavGraphBuilder::courierGraph),
    Admin("admin", NavGraphBuilder::adminGraph)
}


