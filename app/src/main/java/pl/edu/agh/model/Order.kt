package pl.edu.agh.model

import kotlinx.datetime.LocalDateTime
import pl.edu.agh.data.remote.dto.OrderListViewItemDTO

enum class OrderStatus {
    CREATED, IN_PROGRESS, IN_DELIVERY, COMPLETED
}

data class OrderListViewItem(
    val name: String,
    val date: LocalDateTime,
    val status: OrderStatus
) {
    companion object {
        fun fromOrder(orderListViewItemDTO: OrderListViewItemDTO): OrderListViewItem {
            val status = if (orderListViewItemDTO.sendOn == null) {
                OrderStatus.IN_PROGRESS
            } else if (orderListViewItemDTO.deliveredOn == null) {
                OrderStatus.IN_DELIVERY
            } else {
                OrderStatus.COMPLETED
            }
            return OrderListViewItem(orderListViewItemDTO.name, orderListViewItemDTO.placedOn, status)
        }
    }
}