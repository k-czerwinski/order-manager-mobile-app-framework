package pl.edu.agh.implementation.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val role: String,
    val companyId: Int
)