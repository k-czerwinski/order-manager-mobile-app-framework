package pl.edu.agh.implementation.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.model.Company
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.viewmodel.CommonViewModel
import pl.edu.agh.implementation.data.dto.CompanyDTO
import pl.edu.agh.implementation.data.getCompany

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
                state.value = State.Loading
                val companyId = EncryptedSharedPreferencesManager.getCompanyId()
                val company = ApiClient.getCompany(companyId).let(CompanyDTO::toModel)
                state.value = State.Success(company)
            } catch (e: Exception) {
                Log.d("CompanyViewModel", "Unexpected error")
                state.value = State.Error("Unexpected error")
            }
    }
}

