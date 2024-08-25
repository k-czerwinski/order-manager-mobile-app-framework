package pl.edu.agh.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.model.UserRole
import pl.edu.agh.presentation.LoginInvalidCredentialsScreen
import pl.edu.agh.presentation.LoginScreen
import pl.edu.agh.presentation.LoginUnknownErrorScreen
import pl.edu.agh.presentation.sharedViewModel
import pl.edu.agh.presentation.viewmodel.LoginViewModel
import pl.edu.agh.presentation.viewmodel.LoginViewModel.LoginState.*

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = "login_start", route = "auth_route"
    ) {
        composable("login_start") {
            val loginViewModel = it.sharedViewModel<LoginViewModel>(navController)
            LoginScreen(navController, loginViewModel)
        }
        composable("invalid_credentials") {
            LoginInvalidCredentialsScreen {
                navController.navigate("login_start")
            }
        }
        composable("unexpected_error") {
            EncryptedSharedPreferencesManager.clearUserData()
            LoginUnknownErrorScreen {
                navController.navigate("login_start")
            }
        }
    }
}

fun navigateOnLoginState(navController: NavController, loginState: LoginViewModel.LoginState) {
    when (loginState) {
        is Success -> {
            val userRole = loginState.userRole
            when (userRole) {
                UserRole.CLIENT -> navController.navigate("client_route") {
                    popUpTo("auth") {
                        inclusive = true
                    }
                }

                UserRole.COURIER -> navController.navigate("courier_route") {
                    popUpTo("auth") {
                        inclusive = true
                    }
                }
                else -> navController.navigate("unexpected_error")
            }
        }

        is UnexpectedError -> {
            EncryptedSharedPreferencesManager.clearUserData()
            navController.navigate("unexpected_error")
        }
        is InvalidCredentials -> {
            EncryptedSharedPreferencesManager.clearUserData()
            navController.navigate("invalid_credentials")
        }
        else -> {} /** this cases was handled in [LoginScreen] **/
    }
}
