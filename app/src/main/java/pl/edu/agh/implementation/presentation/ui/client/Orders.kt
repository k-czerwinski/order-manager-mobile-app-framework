package pl.edu.agh.implementation.presentation.ui.client

import pl.edu.agh.implementation.presentation.viewmodel.OrderCreateViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.edu.agh.R
import pl.edu.agh.framework.model.Product
import pl.edu.agh.framework.presentation.ui.common.DismissButtonDialog
import pl.edu.agh.framework.presentation.ui.common.ProductList

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
    DismissButtonDialog(R.drawable.ic_order_created,
        stringResource(R.string.order_confirmed_dialog_title),
        stringResource(R.string.order_confirmed_dialog_description),
        backToOrderListView,
        stringResource(R.string.order_dialog_to_list_button)
        )
}

@Composable
fun NoProductSelectedDialog(backToNewOrderScreen: () -> Unit) {
    DismissButtonDialog(R.drawable.error,
        stringResource(R.string.order_new_no_product_selected_dialog_title),
        stringResource(R.string.order_new_no_product_selected_dialog_description),
        backToNewOrderScreen,
        stringResource(R.string.order_new_no_product_selected_dialog_button))
}
