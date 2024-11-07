@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package pl.edu.agh.framework.presentation.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.edu.agh.R

@Composable
fun AppScreen(topBar: @Composable () -> Unit, content: @Composable () -> Unit) {
    Scaffold(
        topBar = topBar,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .testTag("appScreenBox")
                    .fillMaxSize()
            ) {
                content()
            }
        }
    )
}


@Composable
fun AppTopBar(
    navController: NavController,
    companyName: String,
    userMenu: @Composable () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                companyName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = { BackNavigationIcon(navController) },
        actions = {
            userMenu()
        },
    )
}

@Composable
fun AppMenu(showMenu: Boolean, onDismissRequest: () -> Unit, menuItems: @Composable () -> Unit) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .width(200.dp)
    ) {
        menuItems()
    }
}

@Composable
fun BackNavigationIcon(navController: NavController) {
    if (navController.previousBackStackEntry != null) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous screen",
                modifier = Modifier.testTag("backNavigationIcon")
            )
        }
    }
}

@Composable
fun CenteredCircularProgressIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.testTag("centeredCircularProgressIndicator")
        )
    }
}

@Composable
fun UnexpectedErrorScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.smth_went_wrong),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = stringResource(R.string.request_try_again),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
fun DismissButtonDialog(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    onDismissButtonClick: () -> Unit,
    onDismissButtonText: String
) {
    AlertDialog(
        icon = {
            Icon(
                painterResource(icon),
                contentDescription = title,
                modifier = Modifier.testTag("dismissButtonDialogIcon")
            )
        },
        title = {
            Text(title)
        },
        text = {
            Text(description)
        },
        onDismissRequest = onDismissButtonClick,
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismissButtonClick
            ) {
                Text(onDismissButtonText)
            }
        }
    )
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    minLength: Int,
    maxLength: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var firstTouched = remember { mutableStateOf(true) }
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            firstTouched.value = false
        },
        label = { Text(label) },
        isError = !firstTouched.value && value.length < minLength || value.length > maxLength,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation
    )
    if (!firstTouched.value && value.length < minLength) {
        Text(
            stringResource(R.string.min_length_text_validation_message, minLength),
            color = MaterialTheme.colorScheme.error
        )
    }
    if (!firstTouched.value && value.length > maxLength) {
        Text(
            stringResource(R.string.max_length_text_validation_message, maxLength),
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun <T> SelectableDropdown(selectedEntry: String, onEntrySelected: (T) -> Unit, entries: List<T>, placeHolder: String, label: String) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedEntry,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            placeholder = { Text(placeHolder) },
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .testTag("selectableDropdownEntryField"),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Transparent,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            entries.forEach { entry ->
                DropdownMenuItem(text = { Text(text = entry.toString()) }, onClick = {
                    onEntrySelected(entry)
                    expanded = false
                },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}