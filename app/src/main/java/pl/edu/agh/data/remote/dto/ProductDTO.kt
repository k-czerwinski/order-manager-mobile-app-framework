package pl.edu.agh.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDTO(
    val id: Int,
    val name: String,
    val price: String,
    val description: String
)