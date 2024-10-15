package pl.edu.agh.implementation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.remote.HttpResponseException
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.implementation.data.addProduct
import pl.edu.agh.implementation.data.dto.ProductCreateDTO
import java.math.BigDecimal

class ProductCreateViewModel : ViewModel() {
    private val _productCreateState =
        MutableStateFlow<ProductCreateState>(ProductCreateState.Initial)
    val productCreateState: StateFlow<ProductCreateState> = _productCreateState

    fun addProduct(
        name: String,
        price: BigDecimal,
        description: String
    ) {
        viewModelScope.launch {
            if (!validatePrice(price)) {
                _productCreateState.value = ProductCreateState.Error("Invalid price format")
                return@launch
            }
            _productCreateState.value = ProductCreateState.Loading

            val product = ProductCreateDTO(
                name = name,
                price = price,
                description = description
            )
            val companyId = EncryptedSharedPreferencesManager.getCompanyId()
            val adminId = EncryptedSharedPreferencesManager.getUserId()

            try {
                ApiClient.addProduct(companyId, adminId, product)
                _productCreateState.value = ProductCreateState.Success
            } catch (e: HttpResponseException) {
                Log.d("HttpResponseException", e.message!!)
                _productCreateState.value = ProductCreateState.Error(e.message!!)
            } catch (e: Exception) {
                Log.d("Exception", e.message ?: "Unknown error")
                _productCreateState.value = ProductCreateState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun validatePrice(price: BigDecimal?): Boolean {
        return price != null && price >= BigDecimal.ZERO
    }

    sealed class ProductCreateState {
        object Initial : ProductCreateState()
        object Loading : ProductCreateState()
        object Success : ProductCreateState()
        data class Error(val message: String) : ProductCreateState()
    }
}
