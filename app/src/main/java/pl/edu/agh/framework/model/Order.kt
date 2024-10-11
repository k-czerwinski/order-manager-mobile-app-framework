package pl.edu.agh.framework.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

enum class OrderStatus {
    CREATED, IN_PROGRESS, IN_DELIVERY, COMPLETED;
}

data class OrderListViewItem(
    val id: Int,
    val name: String,
    val date: LocalDateTime,
    val status: OrderStatus
)

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
    val totalPrice: BigDecimal,
    val status: OrderStatus
) {
    companion object {
        private val dateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
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
}