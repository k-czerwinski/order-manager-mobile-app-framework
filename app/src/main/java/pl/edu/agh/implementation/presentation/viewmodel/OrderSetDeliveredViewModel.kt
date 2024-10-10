package pl.edu.agh.implementation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.model.Order
import pl.edu.agh.implementation.data.markOrderAsDelivered

class OrderSetDeliveredViewModel : ViewModel() {
    private val _orderDeliveredState =
        MutableStateFlow<OrderDeliveredState>(OrderDeliveredState.Initial)
    val orderDeliveredState: StateFlow<OrderDeliveredState> = _orderDeliveredState

    fun setOrderDelivered(order: Order) {
        _orderDeliveredState.value = OrderDeliveredState.Loading
        viewModelScope.launch {
            if (order.courierId == null) {
                _orderDeliveredState.value = OrderDeliveredState.Error("Order is not assigned to any courier")
                return@launch
            }

            try {
                ApiClient.markOrderAsDelivered(order.companyId, order.courierId, order.id)
                _orderDeliveredState.value = OrderDeliveredState.Success
            } catch (e: Exception) {
                _orderDeliveredState.value = OrderDeliveredState.Error("Failed to set order as delivered")
            }
        }
    }

    fun resetOrderDeliveredState() {
        _orderDeliveredState.value = OrderDeliveredState.Initial
    }

    sealed class OrderDeliveredState {
        object Initial : OrderDeliveredState()
        object Loading : OrderDeliveredState()
        object Success : OrderDeliveredState()
        data class Error(val message: String) : OrderDeliveredState()
    }
}