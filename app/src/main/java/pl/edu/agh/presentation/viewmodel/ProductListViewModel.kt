package pl.edu.agh.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.model.Product

typealias ProductListStateSuccess = CommonViewModel.State.Success<List<Product>>
typealias ProductListStateError = CommonViewModel.State.Error

class ProductListViewModel : CommonListViewModel<Product>() {
    val productsListState: StateFlow<State<List<Product>>> = state

    init {
        viewModelScope.launch {
            fetchData()
        }
    }

    override suspend fun fetchData() {
        try {
            val companyId = EncryptedSharedPreferencesManager.getCompanyId()
            val userRole = EncryptedSharedPreferencesManager.getUserRole()

            val products = ApiClient.getProducts(companyId, userRole).map(Product::fromDTO)
            state.value = State.Success(products)
        } catch (e: Exception) {
            Log.d("ProductListViewModel", "Error fetching orders: ${e.message}")
            state.value = State.Error("Error fetching orders: ${e.message}")
        }
    }
}