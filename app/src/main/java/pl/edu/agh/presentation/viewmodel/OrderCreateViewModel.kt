import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.remote.dto.OrderCreateDTO
import pl.edu.agh.data.remote.dto.OrderProductCreateDTO
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.model.Product
import java.math.BigDecimal

class OrderCreateViewModel : ViewModel() {

    private val _orderCreationState =
        MutableStateFlow<OrderCreationState>(OrderCreationState.Initial)
    val orderCreationState: StateFlow<OrderCreationState> = _orderCreationState

    fun createOrder(orderName: String, products: Map<Product, Int>) {
        _orderCreationState.value = OrderCreationState.Loading
        viewModelScope.launch {
            val clientId = EncryptedSharedPreferencesManager.getUserId()
            val companyId = EncryptedSharedPreferencesManager.getCompanyId()
            val orderCreateDTO = OrderCreateDTO(
                companyId,
                products.map { OrderProductCreateDTO(it.key.id, it.value) },
                clientId,
                orderName
            )

            if (products.isEmpty()) {
                _orderCreationState.value = OrderCreationState.NoProductSelectedError
                return@launch
            }

            try {
                val response = ApiClient.createOrder(orderCreateDTO)
                _orderCreationState.value = OrderCreationState.Success(response.totalPrice)
            } catch (e: Exception) {
                _orderCreationState.value = OrderCreationState.Error("Failed to create order")
            }
        }
    }

    fun resetOrderCreationState() {
        _orderCreationState.value = OrderCreationState.Initial
    }

    sealed class OrderCreationState {
        object Initial : OrderCreationState()
        object Loading : OrderCreationState()
        object NoProductSelectedError: OrderCreationState()
        data class Success(val totalPrice: BigDecimal) : OrderCreationState()
        data class Error(val message: String) : OrderCreationState()
    }
}