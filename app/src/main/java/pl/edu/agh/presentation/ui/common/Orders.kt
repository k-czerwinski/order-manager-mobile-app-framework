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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pl.edu.agh.R
import pl.edu.agh.model.Order
import pl.edu.agh.model.OrderListViewItem
import pl.edu.agh.model.OrderStatus
import pl.edu.agh.model.ProductOrder
import pl.edu.agh.presentation.navigation.ClientNavigation

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
fun OrdersList(orders: List<OrderListViewItem>, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(orders) { order ->
            OrderItem(
                order = order,
                onClick = { navController.navigate(ClientNavigation.createOrderDetailsRoute(order.id)) })
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
            text = stringResource(R.string.user_screen_greeting) + ", $userName!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your orders",
            fontSize = 20.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun OrdersScreen(
    userName: String,
    orders: List<OrderListViewItem>,
    onNewOrderClick: () -> Unit,
    navController: NavController
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
            OrdersList(orders = sortedOrders, navController = navController)
        }

        Button(
            onClick = { onNewOrderClick() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "New Order")
        }
    }
}

@Composable
fun OrderDetailScreen(order: Order) {
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
            ProductItem(productOrder = product)
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
                text = "Total Price: ${order.totalPrice}",
                fontSize = 17.sp,
                color = Color.Gray
            )
            Text(
                text = "Placed On: ${order.getFormattedPlacedOn()}",
                fontSize = 17.sp,
                color = Color.Gray
            )
            Text(
                text = "Delivery: ${order.getFormattedExpectedDeliveryOn()}",
                fontSize = 17.sp,
                color = Color.Gray
            )
        }
        OrderStatusWithDescription(status = order.status)
    }
}

@Composable
fun ProductItem(productOrder: ProductOrder) {
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
                    text = "Quantity: ${productOrder.quantity}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Price per item: ${productOrder.product.price}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Total Price: ${productOrder.totalPrice}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
