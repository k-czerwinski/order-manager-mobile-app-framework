package pl.edu.agh.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.model.Order

class OrderDetailsViewModel(private val orderId: Int) : ViewModel() {
    private val _orderDetailsState = MutableStateFlow<OrderDetailsState>(OrderDetailsState.Empty)
    val orderDetailsState: StateFlow<OrderDetailsState> = _orderDetailsState
    init {
        fetchOrderDetails()
    }

    private fun fetchOrderDetails() {
        viewModelScope.launch {
            try {
                val companyId = EncryptedSharedPreferencesManager.getCompanyId()
                val userRole = EncryptedSharedPreferencesManager.getUserRole()
                val userId = EncryptedSharedPreferencesManager.getUserId()
                val orderDetailsDTO = ApiClient.getOrderDetails(companyId, userRole, userId, orderId)
                _orderDetailsState.value = OrderDetailsState.Success(Order.fromDTO(orderDetailsDTO))
            } catch (e: Exception) {
                Log.d("OrdersViewModel", "Error fetching order details, order id: ${orderId}, message: ${e.message}")
                _orderDetailsState.value = OrderDetailsState.Error("Error fetching orders: ${e.message}")
            }
        }
    }

    sealed class OrderDetailsState {
        data object Empty : OrderDetailsState()
        data class Success(val order: Order) : OrderDetailsState()
        data class Error(val message: String) : OrderDetailsState()
    }
}
