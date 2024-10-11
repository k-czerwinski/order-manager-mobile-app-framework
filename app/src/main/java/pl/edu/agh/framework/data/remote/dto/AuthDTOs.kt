package pl.edu.agh.framework.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String, val companyDomain: String) {
    init {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(password.isNotBlank()) { "Password cannot be blank" }
        require(companyDomain.isNotBlank()) { "Company domain cannot be blank" }
    }
}

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val companyId: Int,
    val userId: Int,
    val userRole: String
)

@Serializable
data class LogoutRequest(val refreshToken: String)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class RefreshTokenResponse(val accessToken: String, val refreshToken: String)
