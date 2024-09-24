package pl.edu.agh.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.model.Product

class ProductListViewModel : ViewModel() {
    private val _productsListState = MutableStateFlow<ProductListState>(ProductListState.Empty)
    val productsListState: StateFlow<ProductListState> = _productsListState

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val companyId = EncryptedSharedPreferencesManager.getCompanyId()
                val userRole = EncryptedSharedPreferencesManager.getUserRole()

                val products = ApiClient.getProducts(companyId, userRole).map(Product::fromDTO)
                _productsListState.value = ProductListState.Success(products)
            } catch (e: Exception) {
                Log.d("ProductListViewModel", "Error fetching orders: ${e.message}")
                _productsListState.value = ProductListState.Error("Error fetching orders: ${e.message}")
            }
        }
    }

    sealed class ProductListState {
        data object Empty : ProductListState()
        data class Success(val products: List<Product>) : ProductListState()
        data class Error(val message: String) : ProductListState()
    }
}