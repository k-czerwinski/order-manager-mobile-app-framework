package pl.edu.agh.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.remote.dto.OrderListViewItemDTO
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager

typealias OrdersListStateSuccess = CommonViewModel.State.Success<List<OrderListViewItemDTO>>

class OrdersListViewModel : CommonListViewModel<OrderListViewItemDTO>() {
    val ordersListState: StateFlow<State<List<OrderListViewItemDTO>>> = state

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
            val companyId = EncryptedSharedPreferencesManager.getCompanyId()
            val userRole = EncryptedSharedPreferencesManager.getUserRole()
            val userId = EncryptedSharedPreferencesManager.getUserId()
            val orders = ApiClient.getOrders(companyId, userRole, userId)
            state.value = State.Success(orders)
        } catch (e: Exception) {
            Log.d("OrdersViewModel", "Error fetching orders: ${e.message}")
            state.value = State.Error("Error fetching orders: ${e.message}")
        }
    }
}