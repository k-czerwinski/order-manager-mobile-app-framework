package pl.edu.agh.framework.data.storage

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import pl.edu.agh.framework.data.remote.dto.LoginResponse
import pl.edu.agh.framework.data.remote.dto.RefreshTokenResponse
import pl.edu.agh.framework.model.UserRoleDependencyInjector
import pl.edu.agh.implementation.model.UserRole
import pl.edu.agh.implementation.model.UserRoleParserInterfaceImpl

@RunWith(JUnit4::class)
class EncryptedSharedPreferencesManagerTest {
    private val loginResponse =
        LoginResponse("accessToken", "refreshToken", 1, 6, UserRole.CLIENT.name)

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            UserRoleDependencyInjector.registerUserRoleParser(UserRoleParserInterfaceImpl)
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            EncryptedSharedPreferencesManager.initialize(context)
        }
    }

    @After
    fun tearDown() {
        EncryptedSharedPreferencesManager.eraseAllData()
    }

    @Test
    fun `test saveLoggedInUser works correctly`() {
        // given
        // when
        EncryptedSharedPreferencesManager.saveLoggedInUser(loginResponse)
        // then
        Assert.assertEquals(
            loginResponse.accessToken,
            EncryptedSharedPreferencesManager.getAccessToken()
        )
        Assert.assertEquals(
            loginResponse.refreshToken,
            EncryptedSharedPreferencesManager.getRefreshToken()
        )
        Assert.assertEquals(
            loginResponse.companyId,
            EncryptedSharedPreferencesManager.getCompanyId()
        )
        Assert.assertEquals(loginResponse.userId, EncryptedSharedPreferencesManager.getUserId())
        Assert.assertEquals(UserRole.CLIENT, EncryptedSharedPreferencesManager.getUserRole())
    }

    @Test
    fun `test eraseAllData works correctly`() {
        EncryptedSharedPreferencesManager.saveLoggedInUser(loginResponse)
        // when
        EncryptedSharedPreferencesManager.eraseAllData()
        // then
        Assert.assertThrows(IllegalStateException::class.java) { EncryptedSharedPreferencesManager.getAccessToken() }
        Assert.assertThrows(IllegalStateException::class.java) { EncryptedSharedPreferencesManager.getRefreshToken() }
        Assert.assertThrows(IllegalStateException::class.java) { EncryptedSharedPreferencesManager.getCompanyId() }
        Assert.assertThrows(IllegalStateException::class.java) { EncryptedSharedPreferencesManager.getUserId() }
        Assert.assertThrows(IllegalStateException::class.java) { EncryptedSharedPreferencesManager.getUserRole() }
    }

    @Test
    fun  `test saveRefreshToken works correctly`() {
        // given
        val refreshTokenResponse = RefreshTokenResponse("newAccessToken", "newRefreshToken")
        // when
        EncryptedSharedPreferencesManager.saveRefreshedTokens(refreshTokenResponse)
        // then
        Assert.assertEquals(refreshTokenResponse.accessToken, EncryptedSharedPreferencesManager.getAccessToken())
        Assert.assertEquals(refreshTokenResponse.refreshToken, EncryptedSharedPreferencesManager.getRefreshToken())
    }
}