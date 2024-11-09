package pl.edu.agh.presentation.ui.common

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.UserRoleDependencyInjector
import pl.edu.agh.framework.presentation.ui.common.LoginInvalidCredentialsScreen
import pl.edu.agh.framework.presentation.ui.common.LoginScreen
import pl.edu.agh.framework.presentation.ui.common.LoginUnknownErrorScreen
import pl.edu.agh.framework.viewmodel.LoginViewModel
import pl.edu.agh.framework.viewmodel.LoginViewModel.LoginState.*
import pl.edu.agh.implementation.model.UserRoleParserInterfaceImpl
import pl.edu.agh.setPrivateField

@RunWith(AndroidJUnit4::class)
class LoginTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            setPrivateField(UserRoleDependencyInjector, "userRoleParserInterface", UserRoleParserInterfaceImpl)
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            EncryptedSharedPreferencesManager.initialize(context)
        }
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<LoginViewModel>(relaxed = true)
    private val loginStateFlow = MutableStateFlow<LoginViewModel.LoginState>(Idle)

    init {
        coEvery { mockViewModel.loginState } returns loginStateFlow
    }

    @Test
    fun loginScreen_displayUserInputFieldsAndButton() {
        setUpLoginScreen()
        composeTestRule.onNodeWithText("Company domain").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsNotEnabled()
    }

    @Test
    fun loginScreen_showsErrorMessagesWhenFieldsAreEmpty() {
        setUpLoginScreen()
        composeTestRule.onNodeWithText("Company domain").performTextInput("test")
        composeTestRule.onNodeWithText("Company domain").performTextClearance()
        composeTestRule.onNodeWithText("This field cannot be empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Company domain").performTextInput("test")

        composeTestRule.onNodeWithText("Username").performTextInput("test")
        composeTestRule.onNodeWithText("Username").performTextClearance()
        composeTestRule.onNodeWithText("This field cannot be empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").performTextInput("test")

        composeTestRule.onNodeWithText("Password").performTextInput("test")
        composeTestRule.onNodeWithText("Password").performTextClearance()
        composeTestRule.onNodeWithText("This field cannot be empty").assertIsDisplayed()
    }

    @Test
    fun loginScreen_enableLoginButtonWhenAllFieldAreFilled() {
        setUpLoginScreen()
        composeTestRule.onNodeWithText("Login").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Company domain").performTextInput("test")
        composeTestRule.onNodeWithText("Login").assertIsNotEnabled()

        composeTestRule.onNodeWithText("Username").performTextInput("test")
        composeTestRule.onNodeWithText("Login").assertIsNotEnabled()

        composeTestRule.onNodeWithText("Password").performTextInput("test")
        composeTestRule.onNodeWithText("Login").assertIsEnabled()
    }

    @Test
    fun loginScreen_showsLoadingIndicatorOnLoginAttempt() {
        setUpLoginScreen()
        composeTestRule.onNodeWithText("Company domain").performTextInput("test_domain")
        composeTestRule.onNodeWithText("Username").performTextInput("test_user")
        composeTestRule.onNodeWithText("Password").performTextInput("test_pass")
        loginStateFlow.value= Loading
        composeTestRule.onNodeWithText("Login").performClick()
        composeTestRule.onNodeWithTag("progressIndicatorLogin").assertIsDisplayed()
    }

    private fun setUpLoginScreen() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController, viewModel = mockViewModel)
        }
    }

    @Test
    fun loginUnknownErrorScreen_displaysErrorMessagesAndButton() {
        var tryAgainClicked = false
        val onTryAgain = { tryAgainClicked = true }
        composeTestRule.setContent {
            LoginUnknownErrorScreen(onTryAgain = onTryAgain)
        }
        composeTestRule.onNodeWithText("Unexpected error").assertIsDisplayed()
        composeTestRule.onNodeWithText("If this problem persists, contact the administrator").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try again")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertTrue(tryAgainClicked)
    }

    @Test
    fun loginInvalidCredentialsScreen_displaysErrorMessagesAndButton() {
        var tryAgainClicked = false
        val onTryAgain = { tryAgainClicked = true }
        composeTestRule.setContent {
            LoginInvalidCredentialsScreen(onTryAgain = onTryAgain)
        }
        composeTestRule.onNodeWithText("Invalid credentials").assertIsDisplayed()
        composeTestRule.onNodeWithText("If this problem persists, contact the administrator").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try again")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertTrue(tryAgainClicked)
    }
}
