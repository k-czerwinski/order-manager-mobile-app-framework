package pl.edu.agh.implementation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.Order
import pl.edu.agh.implementation.data.sendOrder

class OrderSendViewModel : ViewModel() {
    private val _state =
        MutableStateFlow<OrderSendState>(OrderSendState.Initial)
    val orderSendState: StateFlow<OrderSendState> = _state

    fun sendOrder(order: Order) {
        _state.value = OrderSendState.Loading
        viewModelScope.launch {
            if (order.courierId == null) {
                _state.value = OrderSendState.Error("Order is not assigned to any courier")
                return@launch
            }
            try {
                val adminId = EncryptedSharedPreferencesManager.getUserId()
                ApiClient.sendOrder(order.companyId, adminId, order.id, order.courierId)
                _state.value = OrderSendState.Success
            } catch (e: Exception) {
                _state.value = OrderSendState.Error("Failed to set order as delivered")
            }
        }
    }

    fun resetOrderSendState() {
        _state.value = OrderSendState.Initial
    }

    sealed class OrderSendState {
        object Initial : OrderSendState()
        object Loading : OrderSendState()
        object Success : OrderSendState()
        data class Error(val message: String) : OrderSendState()
    }
}