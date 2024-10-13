package pl.edu.agh.implementation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.implementation.data.dto.UserDTO
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.User
import pl.edu.agh.framework.presentation.viewmodel.CommonViewModel
import pl.edu.agh.implementation.data.getCurrentUser

typealias UserStateSuccess = CommonViewModel.State.Success<User>

class UserViewModel : CommonViewModel<User>() {
    val userState: StateFlow<State<User>> = state

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
            val user = ApiClient.getCurrentUser(companyId, userRole).let(UserDTO::toModel)
            state.value = State.Success(user)
        } catch (e: Exception) {
            Log.d("UserViewModel", "Error fetching orders: ${e.message}")
            state.value = State.Error("Error fetching orders: ${e.message}")
        }
    }
}