package pl.edu.agh.implementation.data.dto


import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.edu.agh.framework.data.remote.serialization.BigDecimalSerializer
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
    @SerialName("client")
    val clientId: Int,
    @SerialName("courier")
    val courierId: Int? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val totalPrice: BigDecimal
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
    val product: ProductDTO,
    val quantity: Int
)

@Serializable
data class OrderCreateDTO(
    val companyId: Int,
    val products: List<OrderProductCreateDTO>,
    val clientId: Int,
    val name: String?,
)

@Serializable
data class OrderProductCreateDTO(
    val productId: Int,
    val quantity: Int
)

@Serializable
data class OrderCreateResponseDTO(
    @Serializable(with = BigDecimalSerializer::class)
    val totalPrice: BigDecimal
)

@Serializable
data class ExpectedDeliveryDateTimeDTO(
    val expectedDelivery: LocalDateTime
)
