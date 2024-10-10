package pl.edu.agh.framework.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.implementation.data.dto.UserDTO
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.implementation.data.getCurrentUser

typealias UserStateSuccess = CommonViewModel.State.Success<UserDTO>

class UserViewModel : CommonViewModel<UserDTO>() {
    val userState: StateFlow<State<UserDTO>> = state

    init {
        viewModelScope.launch {
            fetchData()
        }
    }

    override suspend fun fetchData() {
        try {
            state.value = State.Loading
            val companyId = EncryptedSharedPreferencesManager.getCompanyId()
            val userRole = EncryptedSharedPreferencesManager.getUserRole()
            val users = ApiClient.getCurrentUser(companyId, userRole)
            state.value = State.Success(users)
        } catch (e: Exception) {
            Log.d("UserViewModel", "Error fetching orders: ${e.message}")
            state.value = State.Error("Error fetching orders: ${e.message}")
        }
    }
}