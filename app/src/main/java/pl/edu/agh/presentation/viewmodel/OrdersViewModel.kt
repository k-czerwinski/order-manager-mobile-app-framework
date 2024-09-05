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

class OrdersViewModel : ViewModel() {
    private val _ordersState = MutableStateFlow<OrdersState>(OrdersState.Empty)
    val ordersState: StateFlow<OrdersState> = _ordersState

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            try {
                val companyId = EncryptedSharedPreferencesManager.getCompanyId()
                val userRole = EncryptedSharedPreferencesManager.getUserRole()
                val userId = EncryptedSharedPreferencesManager.getUserId()
                val orders = ApiClient.getOrders(companyId, userRole, userId)
                _ordersState.value = OrdersState.Success(orders)
            } catch (e: Exception) {
                Log.d("OrdersViewModel", "Error fetching orders: ${e.message}")
                _ordersState.value = OrdersState.Error("Error fetching orders: ${e.message}")
            }
        }
    }

    sealed class OrdersState {
        data object Empty : OrdersState()
        data class Success(val orderDTOS: List<OrderListViewItemDTO>) : OrdersState()
        data class Error(val message: String) : OrdersState()
    }
}