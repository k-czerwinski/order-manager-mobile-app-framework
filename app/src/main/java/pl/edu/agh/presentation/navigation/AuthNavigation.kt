package pl.edu.agh.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.presentation.ui.common.LoginInvalidCredentialsScreen
import pl.edu.agh.presentation.ui.common.LoginScreen
import pl.edu.agh.presentation.ui.common.LoginUnknownErrorScreen
import pl.edu.agh.presentation.sharedViewModel
import pl.edu.agh.presentation.viewmodel.LoginViewModel
import pl.edu.agh.presentation.viewmodel.LoginViewModel.LoginState.*

enum class AuthNavigation(private val _route: String) {
    LoginStart("login_start"),
    InvalidCredentials("invalid_credentials"),
    UnexpectedError("unexpected_error");

    companion object{
        const val BASE_ROUTE = "auth"
    }
    val route: String get() = "$BASE_ROUTE/$_route"
}

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = AuthNavigation.LoginStart.route, route = AuthNavigation.BASE_ROUTE
    ) {
        composable(AuthNavigation.LoginStart.route) {
            val loginViewModel = it.sharedViewModel<LoginViewModel>(navController)
            LoginScreen(navController, loginViewModel)
        }
        composable(AuthNavigation.InvalidCredentials.route) {
            LoginInvalidCredentialsScreen {
                navController.navigate(AuthNavigation.LoginStart.route)
            }
        }
        composable(AuthNavigation.UnexpectedError.route) {
            EncryptedSharedPreferencesManager.clearUserData()
            LoginUnknownErrorScreen {
                navController.navigate(AuthNavigation.LoginStart.route)
            }
        }
    }
}

fun navigateOnLoginState(navController: NavController, loginState: LoginViewModel.LoginState) {
    when (loginState) {
        is Success -> {
            val userRole = loginState.userRole
            navController.navigate(userRole.navigation.route) {
                popUpTo(AuthNavigation.BASE_ROUTE) {
                    inclusive = true
                }
            }
        }

        is UnexpectedError -> {
            EncryptedSharedPreferencesManager.clearUserData()
            navController.navigate(AuthNavigation.UnexpectedError.route)
        }
        is InvalidCredentials -> {
            EncryptedSharedPreferencesManager.clearUserData()
            navController.navigate(AuthNavigation.InvalidCredentials.route)
        }
        else -> {} /** this cases was handled in [LoginScreen] **/
    }
}
