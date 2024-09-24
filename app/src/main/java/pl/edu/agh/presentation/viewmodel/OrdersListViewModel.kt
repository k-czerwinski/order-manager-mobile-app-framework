package pl.edu.agh.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.remote.dto.OrderListViewItemDTO
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager

class OrdersListViewModel : ViewModel() {
    private val _ordersListState = MutableStateFlow<OrdersListState>(OrdersListState.Empty)
    val ordersListState: StateFlow<OrdersListState> = _ordersListState

    init {
        fetchOrders()
    }

    fun refreshOrders() {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            try {
                val companyId = EncryptedSharedPreferencesManager.getCompanyId()
                val userRole = EncryptedSharedPreferencesManager.getUserRole()
                val userId = EncryptedSharedPreferencesManager.getUserId()
                val orders = ApiClient.getOrders(companyId, userRole, userId)
                _ordersListState.value = OrdersListState.Success(orders)
            } catch (e: Exception) {
                Log.d("OrdersViewModel", "Error fetching orders: ${e.message}")
                _ordersListState.value = OrdersListState.Error("Error fetching orders: ${e.message}")
            }
        }
    }

    sealed class OrdersListState {
        data object Empty : OrdersListState()
        data class Success(val orderDTOS: List<OrderListViewItemDTO>) : OrdersListState()
        data class Error(val message: String) : OrdersListState()
    }
}