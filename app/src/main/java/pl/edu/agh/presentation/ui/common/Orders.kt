package pl.edu.agh.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.edu.agh.R
import pl.edu.agh.model.Order
import pl.edu.agh.model.OrderListViewItem
import pl.edu.agh.model.OrderStatus
import pl.edu.agh.model.Product
import pl.edu.agh.model.ProductOrder
import pl.edu.agh.presentation.ui.client.onProductQuantityChange
import java.math.BigDecimal

@Composable
fun OrderStatusWithDescription(status: OrderStatus) {
    val (iconResource, description) = when (status) {
        OrderStatus.CREATED -> Pair(
            painterResource(id = R.drawable.ic_order_created),
            stringResource(id = R.string.order_status_created)
        )

        OrderStatus.IN_PROGRESS -> Pair(
            painterResource(id = R.drawable.ic_order_in_progress),
            stringResource(id = R.string.order_status_in_progress)
        )

        OrderStatus.IN_DELIVERY -> Pair(
            painterResource(id = R.drawable.ic_order_status_in_delivery),
            stringResource(id = R.string.order_status_in_delivery)
        )

        OrderStatus.COMPLETED -> Pair(
            painterResource(id = R.drawable.ic_order_completed),
            stringResource(id = R.string.order_status_completed)
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = iconResource,
            contentDescription = status.name,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun OrderItem(order: OrderListViewItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = order.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.date.date.toString(),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            OrderStatusWithDescription(status = order.status)
        }
    }
}

@Composable
fun OrdersList(orders: List<OrderListViewItem>, navigateToOrderDetails: (orderId: Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(orders) { order ->
            OrderItem(
                order = order,
                onClick = { navigateToOrderDetails(order.id) })
        }
    }
}

@Composable
fun OrderListGreetings(userName: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.user_screen_greeting, userName),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.user_screen_orders),
            fontSize = 20.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun OrderListScreen(
    userName: String,
    orders: List<OrderListViewItem>,
    navigateToOrderDetails: (orderId: Int) -> Unit,
    bottomButton: @Composable () -> Unit
) {
    val sortedOrders = orders.sortedByDescending { it.date }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OrderListGreetings(userName)
            Spacer(modifier = Modifier.height(16.dp))
            OrdersList(orders = sortedOrders, navigateToOrderDetails = navigateToOrderDetails)
        }
        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()) {
            bottomButton()
        }
    }
}

@Composable
fun OrderDetailScreen(order: Order, actionButtons: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OrderSummary(order = order)
            Spacer(modifier = Modifier.height(16.dp))
            ProductOrderList(products = order.products)
        }
        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()) {
            actionButtons()
        }
    }
}

@Composable
fun ProductOrderList(products: List<ProductOrder>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductOrderItem(productOrder = product)
        }
    }
}

@Composable
fun OrderSummary(order: Order) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = order.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.order_total_price, order.totalPrice),
                fontSize = 17.sp,
                color = Color.Gray
            )
            Text(
                text = stringResource(R.string.order_placed_on, order.getFormattedPlacedOn()),
                fontSize = 17.sp,
                color = Color.Gray
            )
            Text(
                text =
                if (order.deliveredOn != null) stringResource(
                    R.string.order_delivered_on,
                    order.getFormattedDeliveredOn()
                ) else stringResource(
                    R.string.order_expected_delivery_on, order.getFormattedExpectedDeliveryOn()
                ),
                fontSize = 17.sp,
                color = Color.Gray
            )
        }
        OrderStatusWithDescription(status = order.status)
    }
}

@Composable
fun ProductOrderItem(productOrder: ProductOrder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = productOrder.product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = productOrder.product.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(0.7f),
                    maxLines = 3
                )
            }

            Column(
                modifier = Modifier
                    .wrapContentWidth(Alignment.End),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(R.string.quantity, productOrder.quantity),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = stringResource(R.string.price_per_item, productOrder.product.price),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = stringResource(R.string.total_price, productOrder.totalPrice),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
fun ProductItemPreview() {
    var quantity by remember { mutableIntStateOf(0) }
    ProductItem(
        product = Product(1, "Test", BigDecimal(1), "test"),
        onQuantityChange = { quantity = it })
    Text(text = "Quantity: $quantity")
}

@Composable
fun ProductItem(product: Product, onQuantityChange: (Int) -> Unit, quantity: Int = 0) {
    var currentQuantity by remember { mutableIntStateOf(quantity) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.product_price, product.price),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(0.7f),
                    maxLines = 3
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (currentQuantity > 0) currentQuantity-- }) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease quantity")
                }
                TextField(
                    value = currentQuantity.toString(),
                    onValueChange = { newValue ->
                        newValue.toIntOrNull()?.let { newQuantity ->
                            if (newQuantity >= 0) {
                                currentQuantity = newQuantity
                            }
                        }
                    },
                    modifier = Modifier
                        .width(60.dp)
                        .padding(0.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
                IconButton(onClick = { currentQuantity++ }) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase quantity")
                }
            }
        }
    }
    onQuantityChange(currentQuantity)
}

@Composable
@Preview
fun ProductListPreview() {
    val products = listOf(
        Product(1, "Product 1", BigDecimal(10.0), "Description 1"),
        Product(2, "Product 2", BigDecimal(20.0), "Description 2"),
        Product(3, "Product 3", BigDecimal(30.0), "Description 3")
    )
    val selectedProducts = remember { mutableStateMapOf<Product, Int>() }
    ProductList(products, selectedProducts)
}

@Composable
fun ProductList(products: List<Product>, selectedProducts: MutableMap<Product, Int>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductItem(product, onQuantityChange = {
                onProductQuantityChange(selectedProducts, product, it)
            })
        }
    }
}