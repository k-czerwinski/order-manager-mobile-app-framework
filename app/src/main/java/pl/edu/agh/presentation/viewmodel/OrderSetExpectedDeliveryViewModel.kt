package pl.edu.agh.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDateTime
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.model.Order
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class OrderSetExpectedDeliveryViewModel : ViewModel() {
    private val _orderExpectedDeliveryState =
        MutableStateFlow<OrderExpectedDeliveryState>(OrderExpectedDeliveryState.Initial)
    val orderExpectedDeliveryState: StateFlow<OrderExpectedDeliveryState> = _orderExpectedDeliveryState

    fun setOrderDelivered(order: Order, selectedDateMilliseconds: Long, selectedHour: Int, selectedMinute:Int) {
        _orderExpectedDeliveryState.value = OrderExpectedDeliveryState.Loading
        viewModelScope.launch {
            if (order.courierId == null) {
                _orderExpectedDeliveryState.value = OrderExpectedDeliveryState.Error("Order is not assigned to any courier")
                return@launch
            }
            val selectedDate = LocalDate.ofEpochDay(selectedDateMilliseconds / (24 * 60 * 60 * 1000))
            val selectedDateTime = LocalDateTime.of(selectedDate, LocalTime.of(selectedHour, selectedMinute))

            try {
                ApiClient.setExpectedDeliveryDateTime(order.companyId, order.courierId, order.id, selectedDateTime.toKotlinLocalDateTime())
                _orderExpectedDeliveryState.value = OrderExpectedDeliveryState.Success
            } catch (e: Exception) {
                _orderExpectedDeliveryState.value = OrderExpectedDeliveryState.Error("Failed set expected delivery date and time")
            }
        }
    }

    fun resetOrderExpectedDeliveryState() {
        _orderExpectedDeliveryState.value = OrderExpectedDeliveryState.Initial
    }

    sealed class OrderExpectedDeliveryState {
        object Initial : OrderExpectedDeliveryState()
        object Loading : OrderExpectedDeliveryState()
        object Success : OrderExpectedDeliveryState()
        data class Error(val message: String) : OrderExpectedDeliveryState()
    }
}