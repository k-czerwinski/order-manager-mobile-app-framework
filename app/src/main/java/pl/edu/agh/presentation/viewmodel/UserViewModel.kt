package pl.edu.agh.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.remote.dto.UserDTO
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager

class UserViewModel : ViewModel() {
    private val _userState = MutableStateFlow<UserState>(UserState.Empty)
    val userState: StateFlow<UserState> = _userState

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            try {
                val companyId = EncryptedSharedPreferencesManager.getCompanyId()
                val userRole = EncryptedSharedPreferencesManager.getUserRole()
                val orders = ApiClient.getCurrentUser(companyId, userRole)
                _userState.value = UserState.Success(orders)
            } catch (e: Exception) {
                Log.d("UserViewModel", "Error fetching orders: ${e.message}")
                _userState.value = UserState.Error("Error fetching orders: ${e.message}")
            }
        }
    }

    sealed class UserState {
        data object Empty : UserState()
        data class Success(val userDTO: UserDTO) : UserState()
        data class Error(val message: String) : UserState()
    }
}