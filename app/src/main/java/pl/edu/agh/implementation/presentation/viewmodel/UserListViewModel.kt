package pl.edu.agh.implementation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.UserListViewItem
import pl.edu.agh.framework.viewmodel.CommonListViewModel
import pl.edu.agh.framework.viewmodel.CommonViewModel
import pl.edu.agh.implementation.data.dto.UserListViewItemDTO
import pl.edu.agh.implementation.data.getUsers
import pl.edu.agh.implementation.model.UserRole

typealias UserListStateSuccess = CommonViewModel.State.Success<List<UserListViewItem>>

class CourierListViewModel : UserListViewModel(UserRole.COURIER)

open class UserListViewModel(private val requiredRole: UserRole? = null) :
    CommonListViewModel<UserListViewItem>() {
    val userListState: StateFlow<State<List<UserListViewItem>>> = state

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            fetchData()
        }
    }

    override suspend fun fetchData() {
        try {
            state.value = State.Loading
            val companyId = EncryptedSharedPreferencesManager.getCompanyId()
            val userRole = EncryptedSharedPreferencesManager.getUserRole()
            val userId = EncryptedSharedPreferencesManager.getUserId()
            val users = ApiClient.getUsers(companyId, userRole as UserRole, userId, requiredRole)
                .map(UserListViewItemDTO::toModel)
            state.value = State.Success(users)
        } catch (e: Exception) {
            Log.d("OrdersViewModel", "Error fetching orders: ${e.message}")
            state.value = State.Error("Error fetching orders: ${e.message}")
        }
    }
}