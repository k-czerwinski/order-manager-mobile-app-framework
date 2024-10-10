package pl.edu.agh.framework.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import pl.edu.agh.implementation.data.dto.OrderDTO
import pl.edu.agh.implementation.data.dto.OrderListViewItemDTO
import pl.edu.agh.implementation.data.dto.ProductDTO
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

enum class OrderStatus {
    CREATED, IN_PROGRESS, IN_DELIVERY, COMPLETED;

    companion object {
        fun valueOf(sendOn: LocalDateTime?, deliveredOn: LocalDateTime?): OrderStatus {
            return if (deliveredOn != null) {
                COMPLETED
            } else if (sendOn != null) {
                IN_DELIVERY
            } else {
                IN_PROGRESS
            }
        }
    }
}

data class OrderListViewItem(
    val id: Int,
    val name: String,
    val date: LocalDateTime,
    val status: OrderStatus
) {
    companion object {
        fun fromDTO(orderListViewItemDTO: OrderListViewItemDTO): OrderListViewItem {
            val status =
                OrderStatus.valueOf(orderListViewItemDTO.sendOn, orderListViewItemDTO.deliveredOn)
            return OrderListViewItem(
                orderListViewItemDTO.id,
                orderListViewItemDTO.name,
                orderListViewItemDTO.placedOn,
                status
            )
        }
    }
}

data class Order(
    val id: Int,
    val companyId: Int,
    val products: List<ProductOrder>,
    val name: String,
    val placedOn: LocalDateTime,
    val sendOn: LocalDateTime?,
    val deliveredOn: LocalDateTime?,
    val expectedDeliveryOn: LocalDateTime?,
    val clientId: Int,
    val courierId: Int?,
    val totalPrice: BigDecimal
) {
    val status: OrderStatus
        get() = OrderStatus.valueOf(sendOn, deliveredOn)

    companion object {
        private val dateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        fun fromDTO(orderDTO: OrderDTO): Order {
            return Order(
                orderDTO.id,
                orderDTO.companyId,
                orderDTO.products.map { ProductOrder.fromDTO(it.product, it.quantity) },
                orderDTO.name,
                orderDTO.placedOn,
                orderDTO.sendOn,
                orderDTO.deliveredOn,
                orderDTO.expectedDeliveryOn,
                orderDTO.clientId,
                orderDTO.courierId,
                orderDTO.totalPrice
            )
        }
    }

    fun getFormattedPlacedOn(): String {
        return getFormattedDateTime(placedOn)
    }

    fun getFormattedExpectedDeliveryOn(): String {
        return expectedDeliveryOn?.let { getFormattedDateTime(it) } ?: ""
    }

    fun getFormattedDeliveredOn(): String {
        return if (deliveredOn == null) "" else getFormattedDateTime(deliveredOn)
    }

    private fun getFormattedDateTime(localDateTime: LocalDateTime): String {
        return dateTimeFormatter.format(localDateTime.toJavaLocalDateTime())
    }
}

data class ProductOrder(
    val product: Product,
    val quantity: Int
) {
    val totalPrice: BigDecimal
        get() = product.price * quantity.toBigDecimal()

    companion object {
        fun fromDTO(productDTO: ProductDTO, quantity: Int): ProductOrder {
            return ProductOrder(Product.fromDTO(productDTO), quantity)
        }
    }
}