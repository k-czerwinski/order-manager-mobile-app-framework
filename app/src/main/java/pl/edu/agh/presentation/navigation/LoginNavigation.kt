package pl.edu.agh.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.model.UserRole
import pl.edu.agh.presentation.ui.common.LoginInvalidCredentialsScreen
import pl.edu.agh.presentation.ui.common.LoginScreen
import pl.edu.agh.presentation.ui.common.LoginUnknownErrorScreen
import pl.edu.agh.presentation.sharedViewModel
import pl.edu.agh.presentation.viewmodel.LoginViewModel
import pl.edu.agh.presentation.viewmodel.LoginViewModel.LoginState.*

enum class LoginNavigation(route: String) {
    LOGIN_START("login_start"),
    INVALID_CREDENTIALS("invalid_credentials"),
    UNEXPECTED_ERROR("unexpected_error");

    val route: String = AppNavigation.Auth.route + "/" + route
}

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = LoginNavigation.LOGIN_START.route, route = AppNavigation.Auth.route
    ) {
        composable(LoginNavigation.LOGIN_START.route) {
            val loginViewModel = it.sharedViewModel<LoginViewModel>(navController)
            LoginScreen(navController, loginViewModel)
        }
        composable(LoginNavigation.INVALID_CREDENTIALS.route) {
            LoginInvalidCredentialsScreen {
                navController.navigate(LoginNavigation.LOGIN_START.route)
            }
        }
        composable(LoginNavigation.UNEXPECTED_ERROR.route) {
            EncryptedSharedPreferencesManager.clearUserData()
            LoginUnknownErrorScreen {
                navController.navigate(LoginNavigation.LOGIN_START.route)
            }
        }
    }
}

fun navigateOnLoginState(navController: NavController, loginState: LoginViewModel.LoginState) {
    when (loginState) {
        is Success -> {
            val userRole = loginState.userRole
            when (userRole) {
                UserRole.CLIENT -> navController.navigate(AppNavigation.Client.route) {
                    popUpTo(AppNavigation.Auth.route) {
                        inclusive = true
                    }
                }

                UserRole.COURIER -> navController.navigate(AppNavigation.Courier.route) {
                    popUpTo(AppNavigation.Auth.route) {
                        inclusive = true
                    }
                }
                else -> navController.navigate(LoginNavigation.UNEXPECTED_ERROR.route)
            }
        }

        is UnexpectedError -> {
            EncryptedSharedPreferencesManager.clearUserData()
            navController.navigate(LoginNavigation.UNEXPECTED_ERROR.route)
        }
        is InvalidCredentials -> {
            EncryptedSharedPreferencesManager.clearUserData()
            navController.navigate(LoginNavigation.INVALID_CREDENTIALS.route)
        }
        else -> {} /** this cases was handled in [LoginScreen] **/
    }
}
