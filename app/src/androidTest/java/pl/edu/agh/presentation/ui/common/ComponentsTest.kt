package pl.edu.agh.presentation.ui.common

import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.UserRoleDependencyInjector
import pl.edu.agh.framework.presentation.ui.common.AppMenu
import pl.edu.agh.framework.presentation.ui.common.AppScreen
import pl.edu.agh.framework.presentation.ui.common.AppTopBar
import pl.edu.agh.framework.presentation.ui.common.BackNavigationIcon
import pl.edu.agh.framework.presentation.ui.common.CenteredCircularProgressIndicator
import pl.edu.agh.framework.presentation.ui.common.DismissButtonDialog
import pl.edu.agh.framework.presentation.ui.common.InputField
import pl.edu.agh.framework.presentation.ui.common.SelectableDropdown
import pl.edu.agh.framework.presentation.ui.common.UnexpectedErrorScreen
import pl.edu.agh.implementation.model.UserRoleParserInterfaceImpl
import pl.edu.agh.setPrivateField

@RunWith(AndroidJUnit4::class)
class ComponentsTest {
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

    @Test
    fun appScreen_displaysTopBarAndContent() {
        // arrange
        val topBarText = "Top Bar Title"
        val contentText = "Main Content"
        // act
        composeTestRule.setContent {
            AppScreen(
                topBar = { Text(text = topBarText) },
                content = { Text(text = contentText) }
            )
        }
        // assert
        composeTestRule.onNodeWithTag("appScreenBox")
        composeTestRule.onNodeWithText(topBarText).assertIsDisplayed()
        composeTestRule.onNodeWithText(contentText).assertIsDisplayed()
    }

    @Test
    fun appTopBar_displaysCompanyName() {
        // arrange
        val companyName = "Test Company"
        // act
        composeTestRule.setContent {
            AppTopBar(
                navController = rememberNavController(),
                companyName = companyName,
                userMenu = { }
            )
        }
        // assert
        composeTestRule.onNodeWithText(companyName).assertIsDisplayed()
    }

    @Test
    fun appTopBar_displaysUserMenu() {
        // arrange
        val userMenuText = "User Menu Item"
        // act
        composeTestRule.setContent {
            AppTopBar(
                navController = rememberNavController(),
                companyName = "Test Company",
                userMenu = { Text(userMenuText) }
            )
        }
        // assert
        composeTestRule.onNodeWithText(userMenuText).assertIsDisplayed()
    }

    @Test
    fun appMenu_displaysMenuItemsWhenShown() {
        // arrange
        val menuItemText = "Menu Item"
        // act
        composeTestRule.setContent {
            AppMenu(
                showMenu = true,
                onDismissRequest = { },
                menuItems = { Text(menuItemText) }
            )
        }
        // assert
        composeTestRule.onNodeWithText(menuItemText).assertIsDisplayed()
    }

    @Test
    fun appMenu_hidesMenuItemsWhenNotShown() {
        // arrange
        val menuItemText = "Menu Item"
        // act
        composeTestRule.setContent {
            AppMenu(
                showMenu = false,
                onDismissRequest = { },
                menuItems = { Text(menuItemText) }
            )
        }
        // assert
        composeTestRule.onNodeWithText(menuItemText).assertDoesNotExist()
    }

    @Test
    fun backNavigationIcon_isNotDisplayedWhenBackStackEmpty() {
        // act
        composeTestRule.setContent {
            val navController = rememberNavController()
            BackNavigationIcon(navController = navController)
        }
        // assert
        composeTestRule.onNodeWithContentDescription("Previous screen").assertDoesNotExist()
    }

    @Test
    fun centeredCircularProgressIndicator_isDisplayed() {
        // act
        composeTestRule.setContent {
            CenteredCircularProgressIndicator()
        }
        // assert
        composeTestRule.onNodeWithTag("centeredCircularProgressIndicator").assertIsDisplayed()
    }

    @Test
    fun unexpectedErrorScreen_displaysErrorMessages() {
        // act
        composeTestRule.setContent {
            UnexpectedErrorScreen()
        }
        // assert
        composeTestRule.onNodeWithText("Oops! Something went wrong.").assertIsDisplayed()
        composeTestRule.onNodeWithText("We couldn't process your request. Please try again later.")
            .assertIsDisplayed()
    }

    @Test
    fun dismissButtonDialog_displaysVisualElements() {
        // arrange
        val title = "Dialog Title"
        val description = "This is the dialog description."
        // act
        composeTestRule.setContent {
            DismissButtonDialog(
                icon = pl.edu.agh.R.drawable.ic_error,
                title = title,
                description = description,
                onDismissButtonClick = {},
                onDismissButtonText = "Dismiss"
            )
        }
        // assert
        composeTestRule.onNodeWithTag("dismissButtonDialogIcon").assertIsDisplayed()
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(description).assertIsDisplayed()
    }

    @Test
    fun dismissButtonDialog_callsOnDismissButtonClickWhenDismissButtonClicked() {
        // arrange
        var isDismissButtonClicked = false
        val onDismissButtonClick = { isDismissButtonClicked = true }
        // act
        composeTestRule.setContent {
            DismissButtonDialog(
                icon = pl.edu.agh.R.drawable.ic_error,
                title = "Dialog Title",
                description = "This is the dialog description.",
                onDismissButtonClick = onDismissButtonClick,
                onDismissButtonText = "Dismiss"
            )
        }
        composeTestRule.onNodeWithText("Dismiss").performClick()
        // assert
        assertTrue(isDismissButtonClicked)
    }

    @Test
    fun inputField_displaysMinLengthErrorMessage_whenValueIsTooShort() {
        // arrange
        val label = "Username"
        var value = ""
        val onValueChange: (String) -> Unit = { value = it }
        val minLength = 5
        // act
        composeTestRule.setContent {
            InputField(
                value = value,
                onValueChange = onValueChange,
                label = label,
                minLength = minLength,
                maxLength = 20
            )
        }
        composeTestRule.onNodeWithText("Username").performTextInput("four")
        // assert
        composeTestRule.onNodeWithText("This field must have at least $minLength characters")
            .assertIsDisplayed()
    }

    @Test
    fun inputField_displaysMaxLengthErrorMessage_whenValueIsTooLong() {
        // arrange
        val label = "Username"
        var value = "Exceeded max length by one"
        val onValueChange: (String) -> Unit = { value = it }
        val maxLength = 24
        // act
        composeTestRule.setContent {
            InputField(
                value = value,
                onValueChange = onValueChange,
                label = label,
                minLength = 0,
                maxLength = maxLength
            )
        }
        composeTestRule.onNodeWithText("Username").performTextInput("Exceeded max length by one")
        // assert
        composeTestRule.onNodeWithText("This field must have at most $maxLength characters")
            .assertIsDisplayed()
    }

    @Test
    fun inputField_doesNotDisplayErrorMessages_whenValueIsValid() {
        // arrange
        val label = "Username"
        var value = "ValidUsername"
        val onValueChange: (String) -> Unit = { value = it }
        val minLength = 6
        val maxLength = 12
        // act
        composeTestRule.setContent {
            InputField(
                value = value,
                onValueChange = onValueChange,
                label = label,
                minLength = minLength,
                maxLength = maxLength
            )
        }
        // assert
        composeTestRule.onNodeWithText("This field must have at least $minLength characters")
            .assertDoesNotExist()
        composeTestRule.onNodeWithText("This field must have at most $maxLength characters")
            .assertDoesNotExist()
    }

    @Test
    fun selectableDropdown_displaysEntriesInDropdownMenu_whenExpanded() {
        // arrange
        val label = "Dropdown"
        val placeholder = "Select an option"
        val entries = listOf("Option 1", "Option 2", "Option 3")
        var selectedEntry = ""
        // act
        composeTestRule.setContent {
            SelectableDropdown(
                selectedEntry = selectedEntry,
                onEntrySelected = { selectedEntry = it },
                entries = entries,
                placeHolder = placeholder,
                label = label
            )
        }
        composeTestRule.onNodeWithTag("selectableDropdownEntryField").performClick()
        // assert
        entries.forEach {
            composeTestRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun selectableDropdown_updatesSelectedEntry_whenItemSelected() {
        // arrange
        var selectedEntry = ""
        val placeholder = "Select an option"
        val label = "Dropdown"
        val entries = listOf("Option 1", "Option 2", "Option 3")

        // act
        composeTestRule.setContent {
            SelectableDropdown(
                selectedEntry = selectedEntry,
                onEntrySelected = { selectedEntry = it },
                entries = entries,
                placeHolder = placeholder,
                label = label
            )
        }
        composeTestRule.onNodeWithTag("selectableDropdownEntryField").performClick()
        composeTestRule.onNodeWithText("Option 2").performClick()
        // assert
        assertEquals("Option 2", selectedEntry)
    }

}
