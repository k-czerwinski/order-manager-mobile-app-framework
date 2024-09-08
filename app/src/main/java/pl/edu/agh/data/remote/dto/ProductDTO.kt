package pl.edu.agh.data.remote.dto

import kotlinx.serialization.Serializable
import pl.edu.agh.data.remote.serialization.BigDecimalSerializer
import java.math.BigDecimal

@Serializable
data class ProductDTO(
    val id: Int,
    val name: String,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val description: String
)