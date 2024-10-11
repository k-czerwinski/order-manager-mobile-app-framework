package pl.edu.agh.framework.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.remote.ApiClient.login
import pl.edu.agh.framework.data.remote.HttpResponseException
import pl.edu.agh.framework.data.remote.dto.LoginRequest
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.UserRoleInterface
import pl.edu.agh.framework.model.UserRoleDependencyInjector

class LoginViewModel : ViewModel() {

    private val userRoleParser = UserRoleDependencyInjector.getUserRoleParser()
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String, companyDomain: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val loginRequest = LoginRequest(username, password, companyDomain)
                Log.d("LoginViewModel", "Sending login request")
                val loginResponse = ApiClient.login(loginRequest)
                val userRole = userRoleParser.valueOf(loginResponse.userRole)
                EncryptedSharedPreferencesManager.saveLoggedInUser(loginResponse)
                _loginState.value = LoginState.Success(userRole)
            } catch (illegalArgumentException: IllegalArgumentException) {
                _loginState.value =
                    LoginState.EmptyFields(illegalArgumentException.message ?: "Empty fields")
            } catch (httpResponseException: HttpResponseException) {
                _loginState.value = when (httpResponseException.httpStatusCode) {
                    HttpStatusCode.Unauthorized -> LoginState.InvalidCredentials(
                        httpResponseException.message ?: "Invalid credentials"
                    )

                    else -> LoginState.UnexpectedError(
                        httpResponseException.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    sealed class LoginState {
        data object Idle : LoginState()
        data object Loading : LoginState()
        data class Success(val userRole: UserRoleInterface) : LoginState()
        data class UnexpectedError(val message: String) : LoginState()
        data class EmptyFields(val message: String) : LoginState()
        data class InvalidCredentials(val message: String) : LoginState()
    }
}