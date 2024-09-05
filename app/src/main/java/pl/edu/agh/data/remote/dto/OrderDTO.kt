package pl.edu.agh.data.remote.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import pl.edu.agh.data.remote.serialization.BigDecimalSerializer
import java.math.BigDecimal

@Serializable
data class OrderDTO(
    val id: Int,
    val companyId: Int,
    val products: List<ProductOrderDTO>,
    val name: String,
    val placedOn: LocalDateTime,
    val sendOn: LocalDateTime? = null,
    val deliveredOn: LocalDateTime? = null,
    val expectedDeliveryOn: LocalDateTime? = null,
    val courier: UserDTO? = null,
    val totalPrice: String
)

@Serializable
data class OrderListViewItemDTO(
    val id: Int,
    val companyId: Int,
    val clientId: Int,
    val name: String,
    val placedOn: LocalDateTime,
    val sendOn: LocalDateTime?,
    val deliveredOn: LocalDateTime?,
    val expectedDeliveryOn: LocalDateTime?,
    @Serializable(with = BigDecimalSerializer::class)
    val totalPrice: BigDecimal
)

@Serializable
data class ProductOrderDTO(
    val productDTO: ProductDTO,
    val quantity: Int
)
