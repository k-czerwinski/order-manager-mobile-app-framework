package pl.edu.agh.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.remote.dto.Company
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager

typealias CompanySuccessState = CommonViewModel.State.Success<Company>

class CompanyViewModel : CommonViewModel<Company>() {
    val companyState: StateFlow<State<Company>> = state

    init {
        viewModelScope.launch {
            fetchData()
        }
    }

    override suspend fun fetchData() {
            try {
                val companyId = EncryptedSharedPreferencesManager.getCompanyId()
                val company = ApiClient.getCompany(companyId)
                state.value = State.Success(company)
            } catch (e: Exception) {
                Log.d("CompanyViewModel", "Unexpected error")
                state.value = State.Error("Unexpected error")
            }
    }
}

