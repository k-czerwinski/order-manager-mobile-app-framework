package pl.edu.agh.implementation.presentation.ui.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import pl.edu.agh.framework.model.Order
import pl.edu.agh.framework.model.OrderStatus
import pl.edu.agh.framework.model.UserListViewItem
import pl.edu.agh.framework.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.framework.presentation.ui.common.DismissButtonDialog
import pl.edu.agh.framework.presentation.ui.common.OrderSummary
import pl.edu.agh.framework.presentation.ui.common.UserList
import pl.edu.agh.implementation.presentation.navigation.AdminNavigation
import pl.edu.agh.implementation.presentation.viewmodel.OrderSendViewModel
import pl.edu.agh.implementation.presentation.viewmodel.OrdersListViewModel

@Composable
fun AdminOrderDetailsActionButton(
    navController: NavHostController,
    order: Order
) {
    Column {
        if (order.status == OrderStatus.IN_PROGRESS || order.status == OrderStatus.CREATED) {
            Button(
                onClick = {
                    navController.navigate(AdminNavigation.createSendOrderRoute(order.id))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 2.dp)
            ) {
                Text(text = stringResource(R.string.admin_send_order_button))
            }
        }
    }
}

@Composable
fun OrderSendConfirmationDialog(
    courier: UserListViewItem,
    onConfirmButton: () -> Unit,
    onDismissButton: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                painterResource(R.drawable.ic_warning),
                contentDescription = "Order will be send"
            )
        },
        title = {
            Text(text = stringResource(R.string.order_send_confirmation_dialog_title))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.order_send_confirmation_dialog_description,
                    courier.firstName,
                    courier.lastName
                )
            )
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
fun OrderSuccessfullySendDialog(onDismissButton: () -> Unit) {
    DismissButtonDialog(
        R.drawable.ic_action_completed,
        stringResource(R.string.order_send_successfully_dialog_title),
        stringResource(R.string.order_send_successfully_dialog_description),
        onDismissButton,
        stringResource(R.string.order_dialog_to_order_details_button)
    )
}

@Composable
fun OrderCouldNotBeSendDialog(onDismissButton: () -> Unit) {
    DismissButtonDialog(
        R.drawable.ic_error,
        stringResource(R.string.order_could_not_be_send_dialog_title),
        stringResource(R.string.order_could_not_be_send_dialog_description),
        onDismissButton,
        stringResource(R.string.order_could_not_be_send_try_again)
    )
}

@Composable
fun SendOrder(
    navController: NavHostController,
    order: Order,
    availableCouriers: List<UserListViewItem>,
    orderSendViewModel: OrderSendViewModel,
    orderListViewModel: OrdersListViewModel,
) {
    val orderSendState by orderSendViewModel.orderSendState.collectAsState()
    var isConfirmationDialogVisible by remember { mutableStateOf(false) }
    var isConfirmedDialogVisible by remember { mutableStateOf(false) }
    var selectedCourier by remember { mutableStateOf<UserListViewItem?>(null) }
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
            Text(
                text = stringResource(R.string.order_choose_courier_title),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            UserList(users = availableCouriers) {
                isConfirmationDialogVisible = true
                selectedCourier = it
            }
        }
    }

    if (isConfirmationDialogVisible) {
        OrderSendConfirmationDialog(
            courier = selectedCourier!!,
            onConfirmButton = {
                orderSendViewModel.sendOrder(order.copy(courierId = selectedCourier!!.id))
                isConfirmationDialogVisible = false
                isConfirmedDialogVisible = true },
            onDismissButton = {
                isConfirmationDialogVisible = false
            }
        )
    }

    if (isConfirmedDialogVisible) {
        when (orderSendState) {
            is OrderSendViewModel.OrderSendState.Success -> {
                OrderSuccessfullySendDialog(onDismissButton = {
                    selectedCourier = null
                    isConfirmedDialogVisible = false
                    orderListViewModel.loadOrders()
                    navController.navigate(AdminNavigation.createOrderDetailsRoute(order.id)) {
                        popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                            inclusive = true
                        }
                    }
                })
            }

            is OrderSendViewModel.OrderSendState.Error -> {
                OrderCouldNotBeSendDialog(onDismissButton = {
                    orderSendViewModel.resetOrderSendState()
                    isConfirmedDialogVisible = false
                    selectedCourier = null
                })
            }

            is OrderSendViewModel.OrderSendState.Loading, OrderSendViewModel.OrderSendState.Initial -> {
                CenteredCircularProgressIndicator()
            }
        }
    }
}
