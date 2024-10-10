package pl.edu.agh.implementation.data.dto

import kotlinx.serialization.Serializable
import pl.edu.agh.framework.data.remote.serialization.BigDecimalSerializer
import java.math.BigDecimal

@Serializable
data class ProductDTO(
    val id: Int,
    val name: String,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val description: String
)