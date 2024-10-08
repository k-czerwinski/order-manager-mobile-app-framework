package pl.edu.agh.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.model.Order

typealias OrderDetailsStateSuccess = CommonViewModel.State.Success<Order>
typealias OrderDetailsStateError = CommonViewModel.State.Error

class OrderDetailsViewModel(private val orderId: Int) : CommonViewModel<Order>() {
    val orderDetailsState: StateFlow<State<Order>> = state

    init {
        viewModelScope.launch {
            fetchData()
        }
    }

    override suspend fun fetchData() {
        try {
            val companyId = EncryptedSharedPreferencesManager.getCompanyId()
            val userRole = EncryptedSharedPreferencesManager.getUserRole()
            val userId = EncryptedSharedPreferencesManager.getUserId()
            val orderDetailsDTO = ApiClient.getOrderDetails(companyId, userRole, userId, orderId)
            state.value = State.Success(Order.fromDTO(orderDetailsDTO))
        } catch (e: Exception) {
            Log.d(
                "OrdersViewModel",
                "Error fetching order details, order id: ${orderId}, message: ${e.message}"
            )
            state.value = State.Error("Error fetching orders: ${e.message}")
        }
    }
}
