package pl.edu.agh.presentation.ui.client

import OrderCreateViewModel
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
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
import pl.edu.agh.model.Product
import java.math.BigDecimal

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
                    text = stringResource(R.string.product_price) + product.price,
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

@Composable
fun CreateNewOrderScreen(
    products: List<Product>,
    orderCreateViewModel: OrderCreateViewModel,
) {
    val selectedProducts = remember { mutableStateMapOf<Product, Int>() }
    var orderName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.order_new_creating_title),
                fontSize = 20.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            OrderNameField(orderName = orderName, onOrderNameChange = { orderName = it })
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.order_new_choose_products),
                fontSize = 20.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProductList(products, selectedProducts)
        }

        Button(
            onClick = { orderCreateViewModel.createOrder(orderName, selectedProducts) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.order_new_button_confirm))
        }
    }
}

@Composable
fun OrderNameField(orderName: String, onOrderNameChange: (String) -> Unit) {
    OutlinedTextField(
        value = orderName,
        onValueChange = onOrderNameChange,
        label = { Text(stringResource(R.string.order_name_label)) },
        modifier = Modifier.fillMaxWidth()
    )
}

fun onProductQuantityChange(products: MutableMap<Product, Int>, product: Product, quantity: Int) {
    if (quantity <= 0) {
        products.remove(product)
    } else {
        products[product] = quantity
    }
}

@Composable
fun OrderConfirmedDialog(backToOrderListView: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.ic_order_created),
                contentDescription = "Order has been created"
            )
        },
        title = {
            Text(text = stringResource(R.string.order_confirmed_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.order_confirmed_dialog_description))
        },
        onDismissRequest = {
            backToOrderListView()
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = {
                    backToOrderListView()
                }
            ) {
                Text(stringResource(R.string.order_dialog_to_list_button))
            }
        }
    )
}

@Composable
fun NoProductSelectedDialog(backToNewOrderScreen: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.error),
                contentDescription = "Error"
            )
        },
        title = {
            Text(text = stringResource(R.string.order_new_no_product_selected_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.order_new_no_product_selected_dialog_description))
        },
        onDismissRequest = {
            backToNewOrderScreen()
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = {
                    backToNewOrderScreen()
                }
            ) {
                Text(stringResource(R.string.order_new_no_product_selected_dialog_button))
            }
        }
    )
}
