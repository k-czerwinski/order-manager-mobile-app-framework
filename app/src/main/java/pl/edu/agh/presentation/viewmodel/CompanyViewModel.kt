package pl.edu.agh.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.edu.agh.data.remote.ApiClient
import pl.edu.agh.data.remote.dto.Company
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager

class CompanyViewModel : ViewModel() {
    private val _companyState = MutableStateFlow<CompanyState>(CompanyState.Empty)
    val companyState: StateFlow<CompanyState> = _companyState

    init {
        fetchCompanyName()
    }

    private fun fetchCompanyName() {
        viewModelScope.launch {
            try {
                val companyId = EncryptedSharedPreferencesManager.getCompanyId()
                val company = ApiClient.getCompany(companyId)
                _companyState.value = CompanyState.Success(company)
            } catch (e: Exception) {
                Log.d("CompanyViewModel", "Unexpected error")
                _companyState.value = CompanyState.Error("Unexpected error")
            }
        }
    }

    fun resetCompany() {
        _companyState.value = CompanyState.Empty
    }

    sealed class CompanyState {
        data object Empty : CompanyState()
        data class Success(val company: Company) : CompanyState()
        data class Error(val message: String) : CompanyState()
    }
}

