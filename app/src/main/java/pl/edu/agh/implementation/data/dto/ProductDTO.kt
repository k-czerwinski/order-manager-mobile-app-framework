package pl.edu.agh.implementation.data.dto

import kotlinx.serialization.Serializable
import pl.edu.agh.framework.data.remote.serialization.BigDecimalSerializer
import pl.edu.agh.framework.model.Product
import java.math.BigDecimal

@Serializable
data class ProductDTO(
    val id: Int,
    val name: String,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val description: String
) {
    companion object {
        fun toModel(productDTO: ProductDTO) = Product(
            productDTO.id,
            productDTO.name,
            productDTO.price,
            productDTO.description
        )
    }
}

@Serializable
data class ProductCreateDTO(
    val name: String,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val description: String
)