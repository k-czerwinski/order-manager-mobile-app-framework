package pl.edu.agh.implementation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.implementation.data.dto.OrderListViewItemDTO
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.OrderListViewItem
import pl.edu.agh.framework.model.UserRoleInterface
import pl.edu.agh.framework.presentation.viewmodel.CommonListViewModel
import pl.edu.agh.framework.presentation.viewmodel.CommonViewModel
import pl.edu.agh.implementation.data.getOrders
import pl.edu.agh.implementation.model.UserRole

typealias OrdersListStateSuccess = CommonViewModel.State.Success<List<OrderListViewItem>>

class OrdersListViewModel : CommonListViewModel<OrderListViewItem>() {
    val ordersListState: StateFlow<State<List<OrderListViewItem>>> = state

    init {
        loadOrders()
    }

    fun loadOrders() {
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
            val orders = ApiClient.getOrders(companyId, userRole as UserRole, userId).map(OrderListViewItemDTO::toModel)
            state.value = State.Success(orders)
        } catch (e: Exception) {
            Log.d("OrdersViewModel", "Error fetching orders: ${e.message}")
            state.value = State.Error("Error fetching orders: ${e.message}")
        }
    }
}