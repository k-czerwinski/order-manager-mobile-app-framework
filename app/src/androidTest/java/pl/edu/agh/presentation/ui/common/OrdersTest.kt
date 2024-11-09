package pl.edu.agh.presentation.ui.common

import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.Order
import pl.edu.agh.framework.model.OrderListViewItem
import pl.edu.agh.framework.model.OrderStatus
import pl.edu.agh.framework.model.Product
import pl.edu.agh.framework.model.ProductOrder
import pl.edu.agh.framework.model.UserRoleDependencyInjector
import pl.edu.agh.framework.presentation.ui.common.OrderDetailScreen
import pl.edu.agh.framework.presentation.ui.common.OrderListGreetings
import pl.edu.agh.framework.presentation.ui.common.OrderListItem
import pl.edu.agh.framework.presentation.ui.common.OrderListScreen
import pl.edu.agh.framework.presentation.ui.common.OrderStatusWithDescription
import pl.edu.agh.framework.presentation.ui.common.OrderSummary
import pl.edu.agh.framework.presentation.ui.common.OrdersList
import pl.edu.agh.framework.presentation.ui.common.ProductItem
import pl.edu.agh.framework.presentation.ui.common.ProductOrderItem
import pl.edu.agh.framework.presentation.ui.common.SelectProductList
import pl.edu.agh.implementation.model.UserRoleParserInterfaceImpl
import pl.edu.agh.setPrivateField
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class OrdersTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            setPrivateField(UserRoleDependencyInjector, "userRoleParserInterface", UserRoleParserInterfaceImpl)
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            EncryptedSharedPreferencesManager.initialize(context)
        }
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun orderStatusWithDescription_displaysCorrectIconAndDescription_forCreatedStatus() {
        // arrange
        val status = OrderStatus.CREATED
        // act
        composeTestRule.setContent {
            OrderStatusWithDescription(status = status)
        }
        // assert
        composeTestRule.onNodeWithContentDescription(status.name).assertExists()
        composeTestRule.onNodeWithText("Created").assertIsDisplayed()
    }

    @Test
    fun orderStatusWithDescription_displaysCorrectIconAndDescription_forInProgressStatus() {
        // arrange
        val status = OrderStatus.IN_PROGRESS
        // act
        composeTestRule.setContent {
            OrderStatusWithDescription(status = status)
        }
        // assert
        composeTestRule.onNodeWithContentDescription(status.name).assertExists()
        composeTestRule.onNodeWithText("In progress").assertIsDisplayed()
    }

    @Test
    fun orderStatusWithDescription_displaysCorrectIconAndDescription_forInDeliveryStatus() {
        // arrange
        val status = OrderStatus.IN_DELIVERY
        // act
        composeTestRule.setContent {
            OrderStatusWithDescription(status = status)
        }
        // assert
        composeTestRule.onNodeWithContentDescription(status.name).assertExists()
        composeTestRule.onNodeWithText("In delivery").assertIsDisplayed()
    }

    @Test
    fun orderStatusWithDescription_displaysCorrectIconAndDescription_forCompletedStatus() {
        // arrange
        val status = OrderStatus.COMPLETED
        // act
        composeTestRule.setContent {
            OrderStatusWithDescription(status = status)
        }
        // assert
        composeTestRule.onNodeWithContentDescription(status.name).assertExists()
        composeTestRule.onNodeWithText("Completed").assertIsDisplayed()
    }

    @Test
    fun ordersList_displaysOrderItems() {
        // arrange
        val orders = listOf(
            OrderListViewItem(id = 1, name = "Order 1", LocalDateTime(2024, 11, 11, 11, 11), OrderStatus.IN_PROGRESS),
            OrderListViewItem(id = 2, name = "Order 2", LocalDateTime(2024, 11, 11, 11, 11), OrderStatus.IN_DELIVERY),
            OrderListViewItem(id = 3, name = "Order 3", LocalDateTime(2024, 11, 11, 11, 11), OrderStatus.COMPLETED)
        )
        var clickedOrderId: Int? = null
        val navigateToOrderDetails: (Int) -> Unit = { clickedOrderId = it }
        // act
        composeTestRule.setContent {
            OrdersList(orders = orders, navigateToOrderDetails = navigateToOrderDetails)
        }
        // assert
            composeTestRule.onAllNodesWithTag("orderListViewItem").assertCountEquals(3)
    }

    @Test
    fun ordersList_navigatesToOrderDetails_onOrderClick() {
        // arrange
        val orders = listOf(
            OrderListViewItem(id = 1, name = "Order 1", LocalDateTime(2024, 11, 11, 11, 11), OrderStatus.IN_PROGRESS)
            )
        var clickedOrderId: Int? = null
        val navigateToOrderDetails: (Int) -> Unit = { clickedOrderId = it }
        // act
        composeTestRule.setContent {
            OrdersList(orders = orders, navigateToOrderDetails = navigateToOrderDetails)
        }
        composeTestRule.onNodeWithText("Order 1").performClick()
        // assert
        assertEquals(1, clickedOrderId)
    }

    @Test
    fun orderListItem_displaysOrderDetails() {
        // arrange
        val order = OrderListViewItem(
            id = 1,
            name = "Order 1",
            date = LocalDateTime(2024, 11, 9, 10, 30),
            status = OrderStatus.IN_PROGRESS
        )
        // act
        composeTestRule.setContent {
            OrderListItem(order = order, onClick = {})
        }
        // assert
        composeTestRule.onNodeWithText(order.name).assertIsDisplayed()
        composeTestRule.onNodeWithText("2024-11-09").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(OrderStatus.IN_PROGRESS.name).assertIsDisplayed()
    }

    @Test
    fun orderListItem_triggersOnClick_whenClicked() {
        // arrange
        val order = OrderListViewItem(
            id = 1,
            name = "Order 1",
            date = LocalDateTime(2023, 11, 9, 10, 30),
            status = OrderStatus.IN_PROGRESS
        )
        var wasClicked = false
        val onClick: () -> Unit = { wasClicked = true }
        // act
        composeTestRule.setContent {
            OrderListItem(order = order, onClick = onClick)
        }
        composeTestRule.onNodeWithTag("orderListViewItem").performClick()
        // assert
        assertTrue(wasClicked)
    }

    @Test
    fun orderListGreetings_displaysGreetingAndOrdersText() {
        // arrange
        val userName = "John"
        // act
        composeTestRule.setContent {
            OrderListGreetings(userName = userName)
        }
        // assert
        composeTestRule.onNodeWithText("Hello, John!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Orders").assertIsDisplayed()
    }

    @Test
    fun orderListScreen_displaysGreetingAndSortedOrders() {
        // arrange
        val userName = "John"
        val orders = listOf(
            OrderListViewItem(id = 1, name = "Order 1", date = LocalDateTime(2024, 10, 9, 10, 30), status = OrderStatus.IN_PROGRESS),
            OrderListViewItem(id = 2, name = "Order 2", date = LocalDateTime(2024, 11, 9, 10, 30), status = OrderStatus.IN_PROGRESS),
            )
        val bottomButtonContent = "Bottom Button"
        // act
        composeTestRule.setContent {
            OrderListScreen(
                userName = userName,
                orders = orders,
                navigateToOrderDetails = {},
                bottomButton = { Text(bottomButtonContent) }
            )
        }
        // assert
        composeTestRule.onNodeWithTag("orderListGreetings").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("orderListViewItem")[0].assertTextContains("2024-11-09")
        composeTestRule.onAllNodesWithTag("orderListViewItem")[1].assertTextContains("2024-10-09")
        composeTestRule.onNodeWithText(bottomButtonContent).assertIsDisplayed()
    }

    @Test
    fun orderDetailScreen_displaysOrderSummaryProductsAndActionButtons() {
        // arrange
        val order = Order(
            id = 1,
            companyId = 1,
//            products = listOf(
//                ProductOrder(Product(id = 1, name = "Product 1", description = "Description 1", price = BigDecimal("10.00")), 21),
//                ProductOrder(Product(id = 2, name = "Product 2", description = "Description 2", price = BigDecimal("20.00")), 37)
//            ),
            products = emptyList(),
            name = "Order 1",
            placedOn = LocalDateTime(2024, 11, 10, 10, 30),
            sendOn = null,
            deliveredOn = null,
            expectedDeliveryOn = LocalDateTime(2024, 12, 14, 12, 0),
            clientId = 1,
            courierId = null,
            totalPrice = BigDecimal.ZERO,
            status = OrderStatus.CREATED
        )
        val actionButtonText = "Action Button"
        // act
        composeTestRule.setContent {
            OrderDetailScreen(order = order, actionButtons = { Text(actionButtonText) })
        }
        // assert
        composeTestRule.onNodeWithTag("orderSummary").assertIsDisplayed()
        composeTestRule.onNodeWithTag("productOrderList").assertIsDisplayed()
        composeTestRule.onNodeWithText(actionButtonText).assertIsDisplayed()
    }

    @Test
    fun orderSummary_displaysOrderDetailsCorrectly() {
        val order = Order(
            id = 1,
            companyId = 1,
            products = emptyList(),
            name = "Order 1",
            placedOn = LocalDateTime(2024, 11, 5, 12, 30),
            sendOn = LocalDateTime(2024, 11, 6, 12, 0),
            expectedDeliveryOn = LocalDateTime(2024, 11, 10, 12, 0),
            deliveredOn = LocalDateTime(2024, 11, 10, 18, 22),
            clientId = 1,
            courierId = null,
            totalPrice = BigDecimal("128.34"),
            status = OrderStatus.COMPLETED
        )
        // act
        composeTestRule.setContent {
            OrderSummary(order = order)
        }
        // assert
        composeTestRule.onNodeWithTag("orderSummary").assertIsDisplayed()
        composeTestRule.onNodeWithText(order.name).assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Price: 128.34").assertIsDisplayed()
        composeTestRule.onNodeWithText("Placed on: 2024-11-05 12:30").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delivered: 2024-11-10 18:22").assertIsDisplayed()
        composeTestRule.onNodeWithText("Completed").assertIsDisplayed()
    }

    @Test
    fun orderSummary_displaysExpectedDeliveryDateWhenNotDelivered() {
        val order = Order(
            id = 1,
            companyId = 1,
            products = emptyList(),
            name = "Order 1",
            placedOn = LocalDateTime(2024, 11, 5, 12, 30),
            sendOn = LocalDateTime(2024, 11, 6, 12, 0),
            expectedDeliveryOn = LocalDateTime(2024, 11, 10, 12, 30),
            deliveredOn = null,
            clientId = 1,
            courierId = null,
            totalPrice = BigDecimal("128.34"),
            status = OrderStatus.IN_DELIVERY
        )
        // act
        composeTestRule.setContent {
            OrderSummary(order = order)
        }
        // assert
        composeTestRule.onNodeWithText("Placed on: 2024-11-05 12:30").assertIsDisplayed()
        composeTestRule.onNodeWithText("Expected delivery: 2024-11-10 12:30").assertIsDisplayed()
        composeTestRule.onNodeWithText("In delivery").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delivered: ").assertIsNotDisplayed()
    }

    @Test
    fun productOrderItem_displaysProductOrderDetailsCorrectly() {
        // arrange
        val product = Product(
            id = 1,
            name = "Sample Product",
            description = "This is a sample product description",
            price = BigDecimal("19.99")
        )
        val productOrder = ProductOrder(
            product = product,
            quantity = 3
        )
        // act
        composeTestRule.setContent {
            ProductOrderItem(productOrder = productOrder)
        }
        // assert
        composeTestRule.onNodeWithText("Sample Product").assertIsDisplayed()
        composeTestRule.onNodeWithText("This is a sample product description").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quantity: 3").assertIsDisplayed()
        composeTestRule.onNodeWithText("Price per item: 19.99").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total price: 59.97").assertIsDisplayed()
    }

    @Test
    fun productOrderItem_displaysProductDescriptionWithMaxThreeLines() {
        // arrange
        val product = Product(
            id = 2,
            name = "Long Description Product",
            description = "A very long product description that should be truncated after three lines of text. This is meant to ensure that the description doesn't exceed the specified line limit for visual clarity.",
            price = BigDecimal("9.99")
        )
        val productOrder = ProductOrder(
            product = product,
            quantity = 1
        )
        // act
        composeTestRule.setContent {
            ProductOrderItem(productOrder = productOrder)
        }
        // assert
        composeTestRule.onNodeWithText("A very long product description that should be truncated after three lines of text. This is meant to ensure that the description doesn't exceed the specified line limit for visual clarity.")
            .assertIsDisplayed().assertHeightIsEqualTo(49.dp)
    }

    @Test
    fun productItem_displaysProductDetailsCorrectly() {
        // arrange
        val product = Product(
            id = 1,
            name = "Test Product",
            description = "Test Description",
            price = BigDecimal("12.99")
        )
        // act
        composeTestRule.setContent {
            ProductItem(product = product, onQuantityChange = {}, quantity = 0)
        }
        // assert
        composeTestRule.onNodeWithText("Test Product").assertIsDisplayed()
        composeTestRule.onNodeWithText("Price: 12.99").assertIsDisplayed()
    }

    @Test
    fun productItem_shouldIncreaseQuantity_onButtonIncrementsValue() {
        // arrange
        var updatedQuantity = 0
        val product = Product(id = 1, name = "Test Product", description = "Test Description", price = BigDecimal("10.99"))
        composeTestRule.setContent {
            ProductItem(product = product, onQuantityChange = { updatedQuantity = it }, quantity = 1)
        }
        // act
        composeTestRule.onNodeWithContentDescription("Increase quantity").performClick()
        // assert
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
        assertEquals(2, updatedQuantity)
    }

    @Test
    fun productItem_decreaseQuantity_buttonDecrementsValue() {
        // arrange
        var updatedQuantity = 3
        val product = Product(id = 2, name = "Sample Product", description = "Test Description", price = BigDecimal("15.99"))
        composeTestRule.setContent {
            ProductItem(product = product, onQuantityChange = { updatedQuantity = it }, quantity = 3)
        }
        // act
        composeTestRule.onNodeWithContentDescription("Decrease quantity").performClick()
        // assert
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
        assertEquals(2, updatedQuantity)
    }

    @Test
    fun productItem_textField_updatesQuantityWhenValidNumberEntered() {
        // arrange
        var updatedQuantity = 0
        val product = Product(id = 3, name = "Test Product", description = "Test Description", price = BigDecimal("20.00"))
        composeTestRule.setContent {
            ProductItem(product = product, onQuantityChange = { updatedQuantity = it }, quantity = 1)
        }
        // act
        composeTestRule.onNodeWithText("1").performTextReplacement("5")
        composeTestRule.onNodeWithText("Test Product").performClick()
        // assert
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
        assertEquals(5, updatedQuantity)
    }
    @Test
    fun selectProductList_displaysAllProducts() {
        // arrange
        val products = listOf(
            Product(id = 1, name = "Product A", description = "Test Description", price = BigDecimal("10.99")),
            Product(id = 2, name = "Product B", description = "Test Description", price = BigDecimal("5.99")),
            Product(id = 3, name = "Product C", description = "Test Description", price = BigDecimal("15.99"))
        )
        val selectedProducts = mutableMapOf<Product, Int>()
        // act
        composeTestRule.setContent {
            SelectProductList(products = products, selectedProducts = selectedProducts)
        }
        // assert
        products.forEach { product ->
            composeTestRule.onNodeWithText(product.name).assertIsDisplayed()
            composeTestRule.onNodeWithText("Price: ${product.price}").assertIsDisplayed()
        }
    }

    @Test
    fun selectProductList_updatesSelectedProductsOnQuantityChange() {
        // arrange
        val products = listOf(
            Product(id = 1, name = "Product A", description = "Test Description", price = BigDecimal("10.99"))
        )
        val selectedProducts = mutableMapOf<Product, Int>()
        // act
        composeTestRule.setContent {
            SelectProductList(products = products, selectedProducts = selectedProducts)
        }
        composeTestRule.onNodeWithContentDescription("Increase quantity").performClick()
        composeTestRule.onNodeWithContentDescription("Increase quantity").performClick()
        composeTestRule.onNodeWithContentDescription("Increase quantity").performClick()
        composeTestRule.onNodeWithContentDescription("Increase quantity").performClick()
        // assert
        assertEquals(3, selectedProducts[products[0]])
    }

    @Test
    fun selectProductList_initialQuantityIsDisplayedCorrectly() {
        // arrange
        val product = Product(id = 1, name = "Product A", description = "Test Description", price = BigDecimal("10.99"))
        val selectedProducts = mutableMapOf(product to 3)
        // act
        composeTestRule.setContent {
            SelectProductList(products = listOf(product), selectedProducts = selectedProducts)
        }
        // assert
        composeTestRule.onNodeWithText("3").assertIsDisplayed()
    }
}
