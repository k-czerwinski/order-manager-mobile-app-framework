package pl.edu.agh.implementation.presentation.ui.courier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pl.edu.agh.R
import pl.edu.agh.framework.model.Order
import pl.edu.agh.framework.model.OrderStatus
import pl.edu.agh.framework.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.framework.presentation.ui.common.DismissButtonDialog
import pl.edu.agh.framework.presentation.ui.common.OrderSummary
import pl.edu.agh.implementation.presentation.navigation.CourierNavigation
import pl.edu.agh.implementation.presentation.viewmodel.OrderSetDeliveredViewModel
import pl.edu.agh.implementation.presentation.viewmodel.OrderSetDeliveredViewModel.OrderDeliveredState
import pl.edu.agh.implementation.presentation.viewmodel.OrderSetExpectedDeliveryViewModel
import pl.edu.agh.implementation.presentation.viewmodel.OrderSetExpectedDeliveryViewModel.OrderExpectedDeliveryState
import pl.edu.agh.implementation.presentation.viewmodel.OrdersListViewModel
import java.time.LocalDate
import java.util.Calendar

@Composable
fun CourierOrderDetailsActionButtons(
    navController: NavHostController,
    order: Order,
    ordersListViewModel: OrdersListViewModel,
    orderSetDeliveredViewModel: OrderSetDeliveredViewModel = viewModel()
) {
    var isSetDeliveredDialogVisible by remember { mutableStateOf(false) }
    var isDeliveredConfirmedDialogVisible by remember { mutableStateOf(false) }
    val orderedDeliveredState by orderSetDeliveredViewModel.orderDeliveredState.collectAsState()

    Column {
        if (order.status == OrderStatus.IN_DELIVERY) {
            Button(
                onClick = { isSetDeliveredDialogVisible = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 2.dp)
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
                is OrderDeliveredState.Success -> OrderSuccessfullyMarkedAsDeliveredDialog(
                    onDismissButton = {
                        isDeliveredConfirmedDialogVisible = false
                        ordersListViewModel.loadOrders()
                        CourierNavigation.navigateToOrderDetailsRoute(navController, order.id)
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
        if (order.status != OrderStatus.COMPLETED) {
            Button(
                onClick = {
                    CourierNavigation.navigateToOrderExpectedDeliveryRoute(navController, order.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 2.dp)
            ) {
                Text(text = stringResource(R.string.courier_update_expected_delivery_button))
            }
        }
    }
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
fun OrderSuccessfullyMarkedAsDeliveredDialog(onDismissButton: () -> Unit) {
    DismissButtonDialog(
        R.drawable.ic_order_completed,
        stringResource(R.string.order_delivered_dialog_title),
        stringResource(R.string.order_delivered_dialog_description),
        onDismissButton,
        stringResource(R.string.order_dialog_to_order_details_button)
    )
}

@Composable
fun OrderCouldNotBeMarkedAsDelivered(
    onDismissButton: () -> Unit
) {
    DismissButtonDialog(
        R.drawable.error,
        stringResource(R.string.order_set_delivered_error_dialog_title),
        stringResource(R.string.order_set_delivered_error_dialog_description),
        onDismissButton,
        stringResource(R.string.order_dialog_to_order_details_button)
    )
}

@Composable
fun OrderExpectedDeliverySetSuccessfullyDialog(onDismissButton: () -> Unit) {
    DismissButtonDialog(
        R.drawable.ic_order_completed,
        stringResource(R.string.order_expected_delivery_set_successfully_dialog_title),
        stringResource(R.string.order_expected_delivery_set_successfully_dialog_description),
        onDismissButton,
        stringResource(R.string.order_dialog_to_order_details_button)
    )
}

@Composable
fun OrderExpectedDeliverySetErrorDialog(onDismissButton: () -> Unit) {
    DismissButtonDialog(
        R.drawable.error,
        stringResource(R.string.order_expected_delivery_set_error_dialog_title),
        stringResource(R.string.order_expected_delivery_set_error_dialog_description),
        onDismissButton,
        stringResource(R.string.order_dialog_to_order_details_button)
    )
}

@Composable
fun OrderSetExpectedDelivery(
    navController: NavHostController,
    order: Order,
    orderSetExpectedDeliveryViewModel: OrderSetExpectedDeliveryViewModel
) {
    val orderSetExpectedDeliveryState by orderSetExpectedDeliveryViewModel.orderExpectedDeliveryState.collectAsState()
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

            when (orderSetExpectedDeliveryState) {
                is OrderExpectedDeliveryState.Initial -> {
                    ExpectedDeliverySetting { selectedDateMilliseconds, selectedHour, selectedMinute ->
                        orderSetExpectedDeliveryViewModel.setOrderDelivered(
                            order,
                            selectedDateMilliseconds,
                            selectedHour,
                            selectedMinute
                        )
                    }
                }

                is OrderExpectedDeliveryState.Loading -> CenteredCircularProgressIndicator()
                is OrderExpectedDeliveryState.Error -> {
                    orderSetExpectedDeliveryViewModel.resetOrderExpectedDeliveryState()
                    OrderExpectedDeliverySetErrorDialog(onDismissButton = {
                        CourierNavigation.navigateToOrderDetailsRoute(navController, order.id)
                    })
                }

                is OrderExpectedDeliveryState.Success -> {
                    OrderExpectedDeliverySetSuccessfullyDialog {
                        CourierNavigation.navigateToOrderDetailsRoute(navController, order.id)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpectedDeliverySetting(onSelectedDateTime: (selectedDateMilliseconds: Long, selectedHour: Int, selectedMinute: Int) -> Unit) {
    val currentDateMillisecond = LocalDate.now().toEpochDay() * 86_400_000
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDateMillisecond,
        initialDisplayMode = DisplayMode.Input,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= currentDateMillisecond
            }
        }
    )
    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.order_set_expected_delivery_label),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TimeInput(
                    state = timePickerState,
                    modifier = Modifier.wrapContentWidth()
                )
            }

            Button(
                onClick = {
                    if (datePickerState.selectedDateMillis != null) {
                        onSelectedDateTime(
                            datePickerState.selectedDateMillis!!,
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.order_expected_delivery_set_button_confirm))
            }
        }
    }
}
