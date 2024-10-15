package pl.edu.agh.implementation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.R
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.remote.HttpResponseException
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.implementation.data.addUser
import pl.edu.agh.implementation.data.dto.UserCreateDTO
import pl.edu.agh.implementation.model.UserRole
import java.util.regex.Pattern

class UserCreateViewModel : ViewModel() {
    private val _userCreateState =
        MutableStateFlow<UserCreateState>(UserCreateState.Initial)
    val userCreateState: StateFlow<UserCreateState> = _userCreateState

    fun addUser(
        firstName: String,
        lastName: String,
        role: UserRole,
        username: String,
        password: String
    ) {
        viewModelScope.launch {

            if (validatePassword(password) != null) {
                _userCreateState.value = UserCreateState.Error("Invalid password")
                return@launch
            }
            _userCreateState.value = UserCreateState.Loading
            val companyId = EncryptedSharedPreferencesManager.getCompanyId()
            val adminId = EncryptedSharedPreferencesManager.getUserId()
            val user = UserCreateDTO(
                firstName = firstName,
                lastName = lastName,
                username = username,
                role = role.name,
                password = password
            )
            try {
                val response = ApiClient.addUser(companyId, adminId, user)
                _userCreateState.value = UserCreateState.Success(response)
            } catch (e: HttpResponseException) {
                if (e.httpStatusCode == HttpStatusCode.Conflict) {
                    _userCreateState.value = UserCreateState.UserNameAlreadyTaken
                } else {
                    Log.d("HttpResponseException", e.message!!)
                    _userCreateState.value = UserCreateState.Error(e.message!!)
                }
            } catch (e: Exception) {
                Log.d("Exception", e.message!!)
                _userCreateState.value = UserCreateState.Error(e.message!!)
            }
        }
    }

    //    if error exist then return error message code, to be displayed with stringResource()
    fun validatePassword(password: String): Int? {
        val passwordPattern = Pattern.compile(
            "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        )
        return when {
            password.isBlank() -> R.string.password_not_empty
            !passwordPattern.matcher(password)
                .matches() -> R.string.password_requirements

            else -> null
        }
    }

    fun resetState() {
        _userCreateState.value = UserCreateState.Initial
    }

    sealed class UserCreateState {
        object Initial : UserCreateState()
        object Loading : UserCreateState()
        object UserNameAlreadyTaken : UserCreateState()
        data class Success(val userId: Int) : UserCreateState()
        data class Error(val message: String) : UserCreateState()
    }
}