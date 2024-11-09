package pl.edu.agh.presentation.ui.common

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.edu.agh.R
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.User
import pl.edu.agh.framework.model.UserListViewItem
import pl.edu.agh.framework.model.UserRoleDependencyInjector
import pl.edu.agh.framework.model.UserRoleInterface
import pl.edu.agh.framework.presentation.ui.common.InfoBlock
import pl.edu.agh.framework.presentation.ui.common.UserDetails
import pl.edu.agh.framework.presentation.ui.common.UserList
import pl.edu.agh.framework.presentation.ui.common.UserListItem
import pl.edu.agh.framework.presentation.ui.common.UserListScreen
import pl.edu.agh.implementation.model.UserRoleParserInterfaceImpl
import pl.edu.agh.implementation.presentation.navigation.CustomNavigation
import pl.edu.agh.setPrivateField

@RunWith(AndroidJUnit4::class)
class UsersTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            setPrivateField(UserRoleDependencyInjector, "userRoleParserInterface", UserRoleParserInterfaceImpl)
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            EncryptedSharedPreferencesManager.initialize(context)
        }
    }
    enum class UserRoleTest(
        override val urlName: String,
        override val displayNameCode: Int,
        override val navigation: CustomNavigation
    ) : UserRoleInterface {
        ADMIN("admin", R.string.admin_role_display_name, mockk()),
    }

    val users = listOf(
        UserListViewItem(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            role = UserRoleTest.ADMIN
        ),
        UserListViewItem(
            id = 2,
            firstName = "Jane",
            lastName = "Smith",
            role =  UserRoleTest.ADMIN
        )
    )

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun userListItem_displaysCorrectUserInformation() {
        // arrange
        val user = users[0]
        // act
        composeTestRule.setContent {
            UserListItem(user = user, onClick = {})
        }
        // assert
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
    }

    @Test
    fun userListItem_callsOnClick_whenClicked() {
        // arrange
        val user = users[0]
        var clicked = false
        // act
        composeTestRule.setContent {
            UserListItem(user = user) {
                clicked = true
            }
        }
        composeTestRule.onNodeWithText("John Doe").performClick()
        // assert
        assertTrue(clicked)
    }
    @Test
    fun userList_displaysUsersCorrectly() {
        // act
        composeTestRule.setContent {
            UserList(users = users, onUserClick = {})
        }
        // assert
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jane Smith").assertIsDisplayed()
    }

    @Test
    fun userList_invokesOnUserClick_onItemClick() {
        // arrange
        var clickedUser: UserListViewItem? = null
        // act
        composeTestRule.setContent {
            UserList(users = users) { user ->
                clickedUser = user
            }
        }
        composeTestRule.onNodeWithText("John Doe").performClick()
        // assert
        assertNotNull(clickedUser)
        assertEquals(1, clickedUser?.id)
        assertEquals("John", clickedUser?.firstName)
        assertEquals("Doe", clickedUser?.lastName)
    }

    @Test
    fun userListScreen_displaysUserListAndBottomButton() {
        // arrange
        var clickedUser: UserListViewItem? = null
        val bottomButton = @Composable {
            Button(onClick = {}) {
                Text(text = "Bottom Button")
            }
        }
        // act
        composeTestRule.setContent {
            UserListScreen(
                users = users,
                onUserClick = { clickedUser = it },
                bottomButton = bottomButton
            )
        }
        // assert
        composeTestRule.onNodeWithText("Users in the current company:").assertIsDisplayed()
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jane Smith").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bottom Button").assertIsDisplayed()

        composeTestRule.onNodeWithText("John Doe").performClick()
        assertNotNull(clickedUser)
        assertEquals("John", clickedUser?.firstName)
        assertEquals("Doe", clickedUser?.lastName)
    }

    @Test
    fun userListScreen_doesNotDisplayUsersWhenEmpty() {
        // arrange
        val users = listOf<UserListViewItem>()
        val bottomButton = @Composable {
            Button(onClick = {}) {
                Text(text = "Bottom Button")
            }
        }
        // act
        composeTestRule.setContent {
            UserListScreen(
                users = users,
                onUserClick = {},
                bottomButton = bottomButton
            )
        }
        // assert
        composeTestRule.onNodeWithText("Bottom Button").assertIsDisplayed()
        composeTestRule.onNodeWithText("John Doe").assertDoesNotExist()
        composeTestRule.onNodeWithText("Jane Smith").assertDoesNotExist()
    }

    @Test
    fun userDetails_displaysUserInformation() {
        // arrange
        val user = User(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            username = "john_doe",
            role = UserRoleTest.ADMIN
        )
        // act
        composeTestRule.setContent {
            UserDetails(user = user)
        }
        // assert
        composeTestRule.onNodeWithText("First name").assertIsDisplayed()
        composeTestRule.onNodeWithText("John").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("john_doe").assertIsDisplayed()
    }

    @Test
    fun infoBlock_displaysTitleAndStringContent() {
        // arrange
        val title = "Name"
        val content = "John Doe"
        // act
        composeTestRule.setContent {
            InfoBlock(title = title, content = content)
        }
        // assert
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(content).assertIsDisplayed()
    }

    @Test
    fun infoBlock_displaysTitleAndComposableContent() {
        // arrange
        val title = "Name"
        val content = @Composable {
            Text(text = "Sample content", modifier = Modifier.testTag("sampleContent"))
        }
        // act
        composeTestRule.setContent {
            InfoBlock(title = title, content = content)
        }
        // assert
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithTag("sampleContent").assertIsDisplayed()
    }
}
