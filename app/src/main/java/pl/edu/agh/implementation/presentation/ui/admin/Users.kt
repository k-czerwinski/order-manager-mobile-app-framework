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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pl.edu.agh.R
import pl.edu.agh.framework.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.framework.presentation.ui.common.DismissButtonDialog
import pl.edu.agh.framework.presentation.ui.common.InputField
import pl.edu.agh.framework.presentation.ui.common.SelectableDropdown
import pl.edu.agh.implementation.model.UserRole
import pl.edu.agh.implementation.presentation.navigation.AdminNavigation
import pl.edu.agh.implementation.presentation.viewmodel.UserCreateViewModel

@Composable
fun UserListBottomButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = stringResource(R.string.new_user_button))
    }
}

@Composable
fun AddUserScreen(
    navController: NavController
) {
    AddUserForm(navController)
}

@Composable
fun AddUserForm(
    navController: NavController,
    userCreateViewModel: UserCreateViewModel = viewModel()
) {
    val userCreateState by userCreateViewModel.userCreateState.collectAsState()

    var firstName by remember { mutableStateOf("") }
    val firstNameMaxLength = 40

    var lastName by remember { mutableStateOf("") }
    val lastNameMaxLength = 40

    var selectedRole by remember { mutableStateOf<UserRole?>(null) }
    val roles = UserRole.entries.toList()

    var username by remember { mutableStateOf("") }
    var isUserNameTaken by remember { mutableStateOf(false) }
    val usernameMaxLength = 40

    var password by remember { mutableStateOf("") }
    var passwordInitialEdit by remember { mutableStateOf(true) }

    val passwordError = userCreateViewModel.validatePassword(password)
    val isFormValid = firstName.isNotBlank() && lastName.isNotBlank() && username.isNotBlank() &&
            passwordError == null
            && firstName.length <= firstNameMaxLength
            && lastName.length <= lastNameMaxLength
            && username.length <= usernameMaxLength

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InputField(
            firstName,
            { firstName = it },
            stringResource(R.string.user_info_first_name),
            1,
            firstNameMaxLength
        )
        InputField(
            lastName,
            { lastName = it },
            stringResource(R.string.user_info_last_name),
            1,
            lastNameMaxLength
        )
        SelectableDropdown(
            selectedRole?.name ?: "",
            { selectedRole = it },
            roles,
            stringResource(R.string.select_role_placeholder),
            stringResource(R.string.select_role_label),
        )
        InputField(
            username,
            { username = it },
            stringResource(R.string.user_info_username),
            1,
            usernameMaxLength
        )
        if (isUserNameTaken) {
            Text(
                stringResource(R.string.username_already_taken),
                color = MaterialTheme.colorScheme.error
            )
        }
        InputField(
            password,
            {
                password = it
                passwordInitialEdit = false
            },
            label = stringResource(R.string.user_info_password),
            minLength = 0,
            maxLength = Int.MAX_VALUE,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if (!passwordInitialEdit && passwordError != null) {
            Text(stringResource(passwordError), color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                userCreateViewModel.addUser(
                    firstName,
                    lastName,
                    selectedRole!!,
                    username,
                    password
                )
            },
            enabled = isFormValid
        ) {
            Text(stringResource(R.string.add_user_button))
        }
        when (userCreateState) {
            is UserCreateViewModel.UserCreateState.Success -> {
                val userId = (userCreateState as UserCreateViewModel.UserCreateState.Success).userId
                UserCreatedSuccessfullyDialog {
                    navController.navigate(AdminNavigation.createUserDetailsRoute(userId)) {
                        popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                            inclusive = true
                        }
                    }
                }
            }

            is UserCreateViewModel.UserCreateState.Loading -> {
                CenteredCircularProgressIndicator()
            }

            is UserCreateViewModel.UserCreateState.UserNameAlreadyTaken -> {
                isUserNameTaken = true
            }

            is UserCreateViewModel.UserCreateState.Error -> {
                UserCouldNotBeCreatedDialog {
                    navController.navigate(AdminNavigation.UserList.route) {
                        popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                            inclusive = true
                        }
                    }
                }
            }

            else -> {
                // Do nothing
            }
        }
    }
}

@Composable
fun UserCreatedSuccessfullyDialog(goToUserView: () -> Unit) {
    DismissButtonDialog(
        R.drawable.ic_action_completed,
        stringResource(R.string.user_has_been_created_dialog_title),
        stringResource(R.string.user_has_been_created_dialog_description),
        goToUserView,
        stringResource(R.string.go_to_user_details_button)
    )
}

@Composable
fun UserCouldNotBeCreatedDialog(toUserList: () -> Unit) {
    DismissButtonDialog(
        R.drawable.ic_error,
        stringResource(R.string.user_could_not_be_created_dialog_title),
        stringResource(R.string.user_could_not_be_created_dialog_description),
        toUserList,
        stringResource(R.string.go_to_user_list_button)
    )
}