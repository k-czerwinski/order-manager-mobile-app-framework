package pl.edu.agh.framework.data.model

import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import pl.edu.agh.framework.model.Order
import pl.edu.agh.framework.model.OrderStatus
import java.math.BigDecimal

class OrderTest{
    private val order = Order(
        id = 1,
        companyId = 1,
        products = emptyList(),
        name = "Order 1",
        placedOn = LocalDateTime(2023, 12, 18, 10, 30),
        sendOn = null,
        deliveredOn = LocalDateTime(2023, 12, 21, 14, 15),
        expectedDeliveryOn = LocalDateTime(2023, 12, 20, 12, 0),
        clientId = 1,
        courierId = null,
        totalPrice = BigDecimal.ZERO,
        status = OrderStatus.CREATED
    )

    @Test
    fun `test getFormattedPlacedOn succeed`() {
        assertEquals("2023-12-18 10:30", order.getFormattedPlacedOn())
    }

    @Test
    fun `test GetFormattedExpectedDeliveryOn succeed`() {
        assertEquals("2023-12-20 12:00", order.getFormattedExpectedDeliveryOn())
    }

    @Test
    fun `test GetFormattedExpectedDeliveryOn converts null to empty string`() {
        val order = Order(
            id = 1,
            companyId = 1,
            products = emptyList(),
            name = "Order 1",
            placedOn = LocalDateTime(2023, 12, 18, 10, 30),
            sendOn = null,
            deliveredOn = null,
            expectedDeliveryOn = null,
            clientId = 1,
            courierId = null,
            totalPrice = BigDecimal.ZERO,
            status = OrderStatus.CREATED
        )
        assertEquals("", order.getFormattedExpectedDeliveryOn())
    }

    @Test
    fun `test getFormattedDeliveredOn succeed`() {
        assertEquals("2023-12-21 14:15", order.getFormattedDeliveredOn())
    }

    @Test
    fun `test getFormattedDeliveredOn converts null to empty string`() {
        val order = Order(
            id = 1,
            companyId = 1,
            products = emptyList(),
            name = "Order 1",
            placedOn = LocalDateTime(2023, 12, 18, 10, 30),
            sendOn = null,
            deliveredOn = null,
            expectedDeliveryOn = null,
            clientId = 1,
            courierId = null,
            totalPrice = BigDecimal.ZERO,
            status = OrderStatus.CREATED
        )
        assertEquals("", order.getFormattedDeliveredOn())
    }
}