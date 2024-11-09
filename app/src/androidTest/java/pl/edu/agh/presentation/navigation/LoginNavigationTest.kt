package pl.edu.agh.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.testing.TestNavHostController
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import pl.edu.agh.R
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.UserRoleDependencyInjector
import pl.edu.agh.framework.model.UserRoleInterface
import pl.edu.agh.framework.presentation.navigation.AuthNavigation
import pl.edu.agh.framework.presentation.navigation.CustomNavigationInterface
import pl.edu.agh.framework.presentation.navigation.authGraph
import pl.edu.agh.framework.presentation.navigation.navigateOnLoginState
import pl.edu.agh.framework.viewmodel.LoginViewModel
import pl.edu.agh.implementation.model.UserRoleParserInterfaceImpl
import pl.edu.agh.setPrivateField

class LoginNavigationTest {
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

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = TestNavHostController(InstrumentationRegistry.getInstrumentation().targetContext)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = AuthNavigation.BASE_ROUTE) {
                authGraph(navController)
                testGraph(navController)
            }
        }
    }

    @Test
    fun `test initial navigation to login start screen`() {
        // assert
        composeTestRule.onNodeWithTag("loginScreen").assertIsDisplayed()
    }

    @Test
    fun `test navigate to invalid credentials screen`() {
        // act
        composeTestRule.runOnIdle {
            val loginState = LoginViewModel.LoginState.InvalidCredentials("Error message")
            navigateOnLoginState(navController, loginState)
        }
        // assert
        composeTestRule.onNodeWithTag("loginInvalidCredentialsScreen").assertIsDisplayed()
    }

    @Test
    fun `test navigate to unexpected error screen`() {
        // act
        composeTestRule.runOnIdle {
            val loginState = LoginViewModel.LoginState.UnexpectedError("Error message")
            navigateOnLoginState(navController, loginState)
        }
        // assert
        composeTestRule.onNodeWithTag("loginUnknownErrorScreen").assertIsDisplayed()
    }

    @Test
    fun `test navigate to success screen`() {
        // act
        composeTestRule.runOnIdle {
            val loginState = LoginViewModel.LoginState.Success(userRole = UserRoleTest.ADMIN)
            navigateOnLoginState(navController, loginState)
        }
        // assert
        composeTestRule.onNodeWithTag("firstScreen").assertIsDisplayed()
    }

    @Test
    fun `test back navigation after error`() {
        // act
        composeTestRule.runOnIdle {
            navController.navigate(AuthNavigation.InvalidCredentials.route)
        }
        // assert
        composeTestRule.onNodeWithText("Invalid credentials").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try again").performClick()
        composeTestRule.onNodeWithTag("loginScreen").assertIsDisplayed()
    }

    @Test
    fun `test pop up to auth base route`() {
        // act
        composeTestRule.runOnIdle {
            val loginState = LoginViewModel.LoginState.Success(userRole = UserRoleTest.ADMIN)
            navigateOnLoginState(navController, loginState)
        }
        // assert
        assert(navController.currentBackStackEntry?.destination?.route != AuthNavigation.BASE_ROUTE)
    }

    enum class CustomNavigationTest(
        override val route: String,
        override val navigationGraph: NavGraphBuilder.(NavHostController) -> Unit
    ) :
        CustomNavigationInterface {
        Admin("admin", NavGraphBuilder::testGraph)
    }

    enum class UserRoleTest(
        override val urlName: String,
        override val displayNameCode: Int,
        override val navigation: CustomNavigationTest
    ) : UserRoleInterface {
        ADMIN("admin", R.string.admin_role_display_name, CustomNavigationTest.Admin),
    }
}

fun NavGraphBuilder.testGraph(navController: NavHostController) {
    navigation(
        startDestination = "admin/firstScreen", route = "admin"
    ) {
        composable("admin/firstScreen") {
            Text(text = "Test", modifier = Modifier.testTag("firstScreen"))
        }
    }
}
