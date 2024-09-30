package pl.edu.agh.presentation.ui.courier

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pl.edu.agh.R
import pl.edu.agh.model.Order
import pl.edu.agh.model.OrderStatus
import pl.edu.agh.presentation.navigation.CourierNavigation
import pl.edu.agh.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.presentation.viewmodel.OrderSetDeliveredViewModel
import pl.edu.agh.presentation.viewmodel.OrderSetDeliveredViewModel.OrderDeliveredState

@Composable
fun CourierOrderDetailsActionButtons(
    navController: NavHostController,
    orderSetDeliveredViewModel: OrderSetDeliveredViewModel,
    order: Order
) {
    var isSetDeliveredDialogVisible by remember { mutableStateOf(false) }
    var isDeliveredConfirmedDialogVisible by remember { mutableStateOf(false) }
    val orderedDeliveredState by orderSetDeliveredViewModel.orderDeliveredState.collectAsState()


    if (order.status == OrderStatus.IN_DELIVERY) {
        Button(
            onClick = { isSetDeliveredDialogVisible = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.courier_set_delivered_button))
        }
    }

    if (isSetDeliveredDialogVisible) {
        OrderMarkAsDeliveredAlert(onConfirmButton = {
            isDeliveredConfirmedDialogVisible = true
            isSetDeliveredDialogVisible = false
            orderSetDeliveredViewModel.setOrderDelivered(order)
        }, onDismissButton = { isSetDeliveredDialogVisible = false })
    }
    if (isDeliveredConfirmedDialogVisible) {
        when (orderedDeliveredState) {
            is OrderDeliveredState.Success -> OrderMarkedAsDelivered(
                onDismissButton = {
                    isDeliveredConfirmedDialogVisible = false
                    navController.navigate(CourierNavigation.createOrderDetailsRoute(order.id)) {
                        popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                            inclusive = true
                        }
                    }
                }
            )

            is OrderDeliveredState.Loading -> CenteredCircularProgressIndicator()
            is OrderDeliveredState.Error, OrderDeliveredState.Initial -> OrderCouldNotBeMarkedAsDelivered(
                onDismissButton = {
                    isDeliveredConfirmedDialogVisible = false
                    orderSetDeliveredViewModel.resetOrderDeliveredState()
                })
        }
    }
    //TODO "Update expected delivery button"
}

@Composable
fun OrderMarkAsDeliveredAlert(onConfirmButton: () -> Unit, onDismissButton: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.warning),
                contentDescription = "Order will be marked as delivered"
            )
        },
        title = {
            Text(text = stringResource(R.string.order_set_delivered_alert_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.order_set_delivered_alert_dialog_description))
        },
        onDismissRequest = onDismissButton,
        confirmButton = {
            TextButton(onClick = onConfirmButton) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissButton
            ) {
                Text(stringResource(R.string.no))
            }
        }
    )
}

@Composable
fun OrderMarkedAsDelivered(onDismissButton: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.ic_order_completed),
                contentDescription = "Order has been marked as delivered"
            )
        },
        title = {
            Text(text = stringResource(R.string.order_delivered_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.order_delivered_dialog_description))
        },
        onDismissRequest = onDismissButton,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissButton) {
                Text(stringResource(R.string.order_dialog_to_list_button))
            }
        }
    )
}

@Composable
fun OrderCouldNotBeMarkedAsDelivered(
    onDismissButton: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.error),
                contentDescription = "Unexpected error when marking order as delivered"
            )
        },
        title = {
            Text(text = stringResource(R.string.order_set_delivered_error_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.order_set_delivered_error_dialog_description))
        },
        onDismissRequest = onDismissButton,
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismissButton
            ) {
                Text(stringResource(R.string.order_set_delivered_error_dialog_dismiss_button))
            }
        }
    )
}
