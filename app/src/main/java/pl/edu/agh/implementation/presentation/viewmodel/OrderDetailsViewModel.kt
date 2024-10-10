package pl.edu.agh.implementation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.Order
import pl.edu.agh.framework.presentation.viewmodel.CommonViewModel
import pl.edu.agh.implementation.data.getOrderDetails

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
            state.value = State.Loading
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