package pl.edu.agh.implementation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.User
import pl.edu.agh.framework.viewmodel.CommonViewModel
import pl.edu.agh.implementation.data.dto.UserDTO
import pl.edu.agh.implementation.data.getUserDetails

typealias UserDetailsSuccessState = CommonViewModel.State.Success<User>

class UserDetailsViewModel(private val userId: Int) : CommonViewModel<User>() {
    val userDetailsState: StateFlow<State<User>> = state

    init {
        viewModelScope.launch {
            fetchData()
        }
    }

    override suspend fun fetchData() {
        try {
            state.value = State.Loading
            val companyId = EncryptedSharedPreferencesManager.getCompanyId()
            val adminId = EncryptedSharedPreferencesManager.getUserId()
            val userDTO = ApiClient.getUserDetails(companyId, adminId, userId)
            state.value = State.Success(UserDTO.toModel(userDTO))
        } catch (e: Exception) {
            Log.d(
                "OrdersViewModel",
                "Error fetching user details, order id: ${userId}, message: ${e.message}"
            )
            state.value = State.Error("Error fetching orders: ${e.message}")
        }
    }
}