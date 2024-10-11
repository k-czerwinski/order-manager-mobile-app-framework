package pl.edu.agh.framework.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface CustomNavigationInterface {
    val route: String
    val navigationGraph: NavGraphBuilder.(NavHostController) -> Unit
}