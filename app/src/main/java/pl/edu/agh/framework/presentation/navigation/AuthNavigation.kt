package pl.edu.agh.framework.presentation.navigation

import androidx.compose.material3.Surface
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.presentation.sharedViewModel
import pl.edu.agh.framework.presentation.ui.common.LoginInvalidCredentialsScreen
import pl.edu.agh.framework.presentation.ui.common.LoginScreen
import pl.edu.agh.framework.presentation.ui.common.LoginUnknownErrorScreen
import pl.edu.agh.framework.presentation.viewmodel.LoginViewModel

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
            Surface {
                LoginScreen(navController, loginViewModel)
            }
        }
        composable(AuthNavigation.InvalidCredentials.route) {
            LoginInvalidCredentialsScreen {
                navController.navigate(AuthNavigation.LoginStart.route)
            }
        }
        composable(AuthNavigation.UnexpectedError.route) {
            EncryptedSharedPreferencesManager.eraseAllData()
            LoginUnknownErrorScreen {
                navController.navigate(AuthNavigation.LoginStart.route)
            }
        }
    }
}

fun navigateOnLoginState(navController: NavController, loginState: LoginViewModel.LoginState) {
    when (loginState) {
        is LoginViewModel.LoginState.Success -> {
            val userRole = loginState.userRole
            navController.navigate(userRole.navigation.route) {
                popUpTo(AuthNavigation.BASE_ROUTE) {
                    inclusive = true
                }
            }
        }

        is LoginViewModel.LoginState.UnexpectedError -> {
            EncryptedSharedPreferencesManager.eraseAllData()
            navController.navigate(AuthNavigation.UnexpectedError.route)
        }
        is LoginViewModel.LoginState.InvalidCredentials -> {
            EncryptedSharedPreferencesManager.eraseAllData()
            navController.navigate(AuthNavigation.InvalidCredentials.route)
        }
        else -> {} /** this cases was handled in [LoginScreen] **/
    }
}
