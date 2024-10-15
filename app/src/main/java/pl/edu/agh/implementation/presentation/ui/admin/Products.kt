package pl.edu.agh.implementation.presentation.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pl.edu.agh.R
import pl.edu.agh.framework.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.framework.presentation.ui.common.DismissButtonDialog
import pl.edu.agh.framework.presentation.ui.common.InputField
import pl.edu.agh.implementation.presentation.navigation.AdminNavigation
import pl.edu.agh.implementation.presentation.viewmodel.ProductCreateViewModel
import pl.edu.agh.implementation.presentation.viewmodel.ProductListViewModel
import java.math.BigDecimal

@Composable
fun CreateProductButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = stringResource(R.string.new_product))
    }
}

@Composable
fun AddProductForm(
    navController: NavController,
    productListViewModel: ProductListViewModel,
    productCreateViewModel: ProductCreateViewModel = viewModel()
) {
    val productCreateState by productCreateViewModel.productCreateState.collectAsState()

    var name by remember { mutableStateOf("") }
    val nameMaxLength = 40

    var price by remember { mutableStateOf("") }
    var isPriceValid by remember { mutableStateOf(true) }

    var description by remember { mutableStateOf("") }
    val descriptionMaxLength = 255

    val isFormValid = name.isNotBlank() && name.length <= nameMaxLength
            && price.isNotBlank() && isPriceValid
            && description.length <= descriptionMaxLength

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InputField(
            value = name,
            onValueChange = { newValue -> name = newValue },
            label = stringResource(R.string.product_name),
            minLength = 1,
            maxLength = nameMaxLength
        )
        InputField(
            value = price,
            onValueChange = { newValue ->
                price = newValue
                isPriceValid = try {
                    BigDecimal(newValue)
                    true
                } catch (e: NumberFormatException) {
                    false
                }
            },
            label = stringResource(R.string.product_price_label),
            minLength = 1,
            maxLength = Int.MAX_VALUE,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (!isPriceValid) {
            Text(
                text = stringResource(R.string.invalid_price_error),
                color = MaterialTheme.colorScheme.error
            )
        }

        InputField(
            value = description,
            onValueChange = { newValue -> description = newValue },
            label = stringResource(R.string.product_description),
            minLength = 0,
            maxLength = descriptionMaxLength
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                productCreateViewModel.addProduct(
                    name = name,
                    price = BigDecimal(price),
                    description = description
                )
            },
            enabled = isFormValid,
        ) {
            Text(stringResource(R.string.add_product_button))
        }

        when (productCreateState) {
            is ProductCreateViewModel.ProductCreateState.Success -> {
                ProductCreatedSuccessfullyDialog(toProductList = {
                    productListViewModel.loadProducts()
                    navController.navigate(AdminNavigation.ProductList.route) {
                        popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                            inclusive = true
                        }
                    }
                })
            }

            is ProductCreateViewModel.ProductCreateState.Loading -> {
                CenteredCircularProgressIndicator()
            }

            is ProductCreateViewModel.ProductCreateState.Error -> {
                ProductCouldNotBeCreatedDialog(toProductList = {
                    navController.navigate(AdminNavigation.ProductList.route) {
                        popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                            inclusive = true
                        }
                    }
                })
            }

            else -> {
                // Do nothing
            }
        }
    }
}

@Composable
fun ProductCreatedSuccessfullyDialog(toProductList: () -> Unit) {
    DismissButtonDialog(
        R.drawable.ic_action_completed, // Adjust this drawable resource if needed
        stringResource(R.string.product_has_been_created_dialog_title),
        stringResource(R.string.product_has_been_created_dialog_description),
        toProductList,
        stringResource(R.string.go_to_product_list_button)
    )
}

@Composable
fun ProductCouldNotBeCreatedDialog(toProductList: () -> Unit) {
    DismissButtonDialog(
        R.drawable.ic_error, // Adjust this drawable resource if needed
        stringResource(R.string.product_could_not_be_created_dialog_title),
        stringResource(R.string.product_could_not_be_created_dialog_description),
        toProductList,
        stringResource(R.string.go_to_product_list_button)
    )
}

