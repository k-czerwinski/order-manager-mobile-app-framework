package pl.edu.agh.implementation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.Product
import pl.edu.agh.framework.presentation.viewmodel.CommonListViewModel
import pl.edu.agh.framework.presentation.viewmodel.CommonViewModel
import pl.edu.agh.implementation.data.getProducts

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
            state.value = State.Loading
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