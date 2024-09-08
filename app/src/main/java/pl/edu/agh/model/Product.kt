package pl.edu.agh.model

import pl.edu.agh.data.remote.dto.ProductDTO
import java.math.BigDecimal

data class Product(
    val id: Int,
    val name: String,
    val price: BigDecimal,
    val description: String
) {

    companion object {
        fun fromDTO(productDTO: ProductDTO) = Product(
            productDTO.id,
            productDTO.name,
            productDTO.price,
            productDTO.description)
    }
}