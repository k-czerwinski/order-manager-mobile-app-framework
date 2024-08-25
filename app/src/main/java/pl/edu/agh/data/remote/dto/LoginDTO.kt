package pl.edu.agh.data.remote.dto

import kotlinx.serialization.Serializable
import pl.edu.agh.model.UserRole

@Serializable
data class LoginRequest(val username: String, val password: String, val companyDomain: String) {
    init {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(password.isNotBlank()) { "Password cannot be blank" }
        require(companyDomain.isNotBlank()) { "Company domain cannot be blank" }
    }
}
@Serializable
data class LoginResponse(val accessToken: String, val companyId: Int, val userId: Int, val userRole: UserRole)
