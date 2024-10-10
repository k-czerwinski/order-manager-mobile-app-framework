package pl.edu.agh.implementation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.remote.dto.OrderListViewItemDTO
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.presentation.viewmodel.CommonListViewModel
import pl.edu.agh.framework.presentation.viewmodel.CommonViewModel
import pl.edu.agh.implementation.data.getOrders

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
            state.value = State.Loading
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