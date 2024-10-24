package pl.edu.agh.implementation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.Order
import pl.edu.agh.framework.viewmodel.CommonViewModel
import pl.edu.agh.implementation.data.dto.OrderDTO
import pl.edu.agh.implementation.data.getOrderDetails
import pl.edu.agh.implementation.model.UserRole

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
            val orderDetailsDTO = ApiClient.getOrderDetails(companyId, userRole as UserRole, userId, orderId)
            state.value = State.Success(OrderDTO.toModel(orderDetailsDTO))
        } catch (e: Exception) {
            Log.d(
                "OrdersViewModel",
                "Error fetching order details, order id: ${orderId}, message: ${e.message}"
            )
            state.value = State.Error("Error fetching orders: ${e.message}")
        }
    }
}