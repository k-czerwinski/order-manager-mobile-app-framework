package pl.edu.agh.implementation.data.dto


import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.edu.agh.framework.data.remote.serialization.BigDecimalSerializer
import pl.edu.agh.framework.model.Order
import pl.edu.agh.framework.model.OrderListViewItem
import pl.edu.agh.framework.model.OrderStatus
import pl.edu.agh.framework.model.OrderStatus.COMPLETED
import pl.edu.agh.framework.model.OrderStatus.IN_DELIVERY
import pl.edu.agh.framework.model.OrderStatus.IN_PROGRESS
import pl.edu.agh.framework.model.ProductOrder
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
) {
    companion object {
        fun toModel(orderDTO: OrderDTO): Order {
            return Order(
                orderDTO.id,
                orderDTO.companyId,
                orderDTO.products.map { ProductOrderDTO.toModel(it.product, it.quantity) },
                orderDTO.name,
                orderDTO.placedOn,
                orderDTO.sendOn,
                orderDTO.deliveredOn,
                orderDTO.expectedDeliveryOn,
                orderDTO.clientId,
                orderDTO.courierId,
                orderDTO.totalPrice,
                OrderStatusConverter.parseOrderStatus(orderDTO.sendOn, orderDTO.deliveredOn)
            )
        }
    }
}

object OrderStatusConverter {
    fun parseOrderStatus(sendOn: LocalDateTime?, deliveredOn: LocalDateTime?): OrderStatus {
        return if (deliveredOn != null) {
            COMPLETED
        } else if (sendOn != null) {
            IN_DELIVERY
        } else {
            IN_PROGRESS
        }
    }
}

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
) {
    companion object {
        fun toModel(orderListViewItemDTO: OrderListViewItemDTO): OrderListViewItem {
            val status =
                OrderStatusConverter.parseOrderStatus(
                    orderListViewItemDTO.sendOn,
                    orderListViewItemDTO.deliveredOn
                )
            return OrderListViewItem(
                orderListViewItemDTO.id,
                orderListViewItemDTO.name,
                orderListViewItemDTO.placedOn,
                status
            )
        }
    }
}

@Serializable
data class ProductOrderDTO(
    val product: ProductDTO,
    val quantity: Int
) {
    companion object {
        fun toModel(productDTO: ProductDTO, quantity: Int): ProductOrder {
            return ProductOrder(ProductDTO.toModel(productDTO), quantity)
        }
    }
}

@Serializable
data class OrderCreateDTO(
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
