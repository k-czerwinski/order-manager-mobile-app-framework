package pl.edu.agh.framework.data.remote

import androidx.test.platform.app.InstrumentationRegistry
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import pl.edu.agh.framework.data.remote.ApiClient.login
import pl.edu.agh.framework.data.remote.ApiClient.logout
import pl.edu.agh.framework.data.remote.dto.LoginRequest
import pl.edu.agh.framework.data.remote.dto.RefreshTokenResponse
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.UserRoleDependencyInjector
import pl.edu.agh.implementation.data.getCompany
import pl.edu.agh.implementation.model.UserRoleParserInterfaceImpl

class ApiClientTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            UserRoleDependencyInjector.registerUserRoleParser(UserRoleParserInterfaceImpl)
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            EncryptedSharedPreferencesManager.initialize(context)
        }
    }

    @Test
    fun `test login works`() = runBlocking {
        // given
        val mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel(
                    """
                    {
                      "accessToken": "accessToken",
                      "refreshToken": "refreshToken",
                      "companyId": 2,
                      "userId": 5,
                      "userRole": "ADMIN"
                    }
                """.trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        ApiClient.initialize(mockEngine)
        // when
        val response = ApiClient.login(
            LoginRequest(
                "username", "password", "company-domain.com"
            )
        )
        Assert.assertEquals("accessToken", response.accessToken)
        Assert.assertEquals("refreshToken", response.refreshToken)
        Assert.assertEquals(2, response.companyId)
        Assert.assertEquals(5, response.userId)
        Assert.assertEquals("ADMIN", response.userRole)
    }

    @Test
    fun `test login should return unatuhroized`() {

        // given
        val mockEngine = MockEngine { request ->
            respond(
                "Unauthorized",
                status = HttpStatusCode.Unauthorized
            )
        }
        ApiClient.initialize(mockEngine)
        // when
        Assert.assertThrows(HttpResponseException::class.java) {
            runBlocking {
                ApiClient.login(LoginRequest("username", "password", "company-domain.com"))
            }
        }
    }

    @Test
    fun `test logout should succeed`() {
        // given
        val mockEngine = MockEngine { request ->
            respond(
                "No Content",
                status = HttpStatusCode.NoContent
            )
        }
        ApiClient.initialize(mockEngine)
        // then
        runBlocking {
            try {
                ApiClient.logout("refresh token")
            } catch (e: Exception) {
                Assert.fail("Exception has been thrown on logout ${e.message}")
            }
        }
    }

    @Test
    fun `test authenticated client add access token to request`(): Unit = runBlocking {
        // given
        val refreshTokenResponse =
            RefreshTokenResponse("accessdfasdfsdafsToken", "refdfsreshdsToksden")
        val mockEngine = MockEngine { request ->
            respond(
                """{
                            "name": "name",
                            "id": 1,
                            "domain": "domain"
                        }""".trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        EncryptedSharedPreferencesManager.saveRefreshedTokens(refreshTokenResponse)
        ApiClient.initialize(mockEngine)
        // when
        ApiClient.getCompany(1)
        // then
        val authorizationHeader = mockEngine.requestHistory[0].headers[HttpHeaders.Authorization]
        Assert.assertEquals("Bearer ${refreshTokenResponse.accessToken}", authorizationHeader)
    }

    @Test
    fun `test ApiClient refresh tokens automatically`(): Unit = runBlocking {
        // given
        val mockEngine = MockEngine { request ->
            if (request.url.encodedPath.endsWith("/refresh-token")) {
                respond(
                    """{
                            "accessToken": "newAccessToken",
                            "refreshToken": "newRefreshToken"
                        }""".trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            } else {
                respondError(HttpStatusCode.Unauthorized)
            }
        }
        EncryptedSharedPreferencesManager.saveRefreshedTokens(RefreshTokenResponse("at", "rt"))
        ApiClient.initialize(mockEngine)
        // when
        try {
            ApiClient.getCompany(1)
        } catch (_: Exception) {
        }
        // then
        Assert.assertEquals("newAccessToken", EncryptedSharedPreferencesManager.getAccessToken())
        Assert.assertEquals("newRefreshToken", EncryptedSharedPreferencesManager.getRefreshToken())
    }
}