package pl.edu.agh.presentation.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import pl.edu.agh.R
import pl.edu.agh.presentation.navigation.navigateOnLoginState
import pl.edu.agh.presentation.viewmodel.LoginViewModel
import pl.edu.agh.presentation.viewmodel.LoginViewModel.LoginState.*

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = LoginViewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var companyDomain by remember { mutableStateOf("") }
    var isUsernameError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var isCompanyDomainError by remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserInputField(
            value = companyDomain,
            onValueChange = {
                companyDomain = it
                isCompanyDomainError = it.isEmpty()
            },
            label = stringResource(R.string.login_company_domain),
            isError = isCompanyDomainError
        )
        UserInputField(
            value = username,
            onValueChange = {
                username = it
                isUsernameError = it.isEmpty()
            },
            label = stringResource(R.string.login_username),
            isError = isUsernameError
        )
        UserInputField(
            value = password,
            onValueChange = {
                password = it
                isPasswordError = it.isEmpty()
            }, label = stringResource(R.string.login_password), isError = isPasswordError,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.login(username, password, companyDomain) },
            modifier = Modifier.fillMaxWidth(),
            enabled = username.isNotEmpty() && password.isNotEmpty() && companyDomain.isNotEmpty()
        ) {
            Text(stringResource(R.string.login_button))
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (loginState) {
            is Loading -> CircularProgressIndicator()
            is Idle -> {}
            else -> {
                companyDomain = ""
                username = ""
                password = ""
                isCompanyDomainError = false
                isUsernameError = false
                isPasswordError = false
                navigateOnLoginState(navController, loginState)
                viewModel.resetLoginState()
            }
        }
    }
}

@Composable
fun LoginInvalidCredentialsScreen(onTryAgain: () -> Unit) {
    LoginErrorScreen(
        errorMessage = stringResource(R.string.login_invalid_credentials),
        errorDescription = stringResource(R.string.login_contact_administrator),
        onTryAgain = onTryAgain
    )
}

@Composable
fun UserInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        isError = isError,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation
    )
    if (isError) {
        Text(
            text = stringResource(R.string.login_empty_field),
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun LoginUnknownErrorScreen(onTryAgain: () -> Unit) {
    LoginErrorScreen(
        errorMessage = stringResource(R.string.login_unexpected_error),
        errorDescription = stringResource(R.string.login_contact_administrator),
        onTryAgain = onTryAgain
    )
}

@Composable
fun LoginErrorScreen(errorMessage: String, errorDescription: String, onTryAgain: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = errorDescription,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onTryAgain
        ) {
            Text(stringResource(R.string.login_try_again))
        }
    }
}